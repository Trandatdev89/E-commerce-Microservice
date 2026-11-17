package com.project01.identityservice.services.impl;

import com.nimbusds.jwt.SignedJWT;
import com.project01.common.core.exception.BusinessException;
import com.project01.common.core.services.RedisService;
import com.project01.common.security.jwt.CustomUserDetail;
import com.project01.common.security.jwt.Role;
import com.project01.event.dto.AccountVerifiedEvent;
import com.project01.event.dto.NewDeviceLoginEvent;
import com.project01.event.dto.VerifyAccountRequest;
import com.project01.identityservice.dto.request.AuthenticationRequest;
import com.project01.identityservice.dto.request.LogoutRequest;
import com.project01.identityservice.dto.request.RefreshTokenRequest;
import com.project01.identityservice.dto.response.AuthenticationResponse;
import com.project01.identityservice.dto.response.IntrospectTokenResponse;
import com.project01.identityservice.dto.response.RegisterDTO;
import com.project01.identityservice.dto.response.RegisterResponse;
import com.project01.identityservice.entity.AccountEntity;
import com.project01.identityservice.entity.AccountRoleEntity;
import com.project01.identityservice.entity.RoleEntity;
import com.project01.identityservice.enums.AccountStatus;
import com.project01.identityservice.exception.ErrorCode;
import com.project01.identityservice.kafka.IdentityProducer;
import com.project01.identityservice.repository.AccountRepository;
import com.project01.identityservice.repository.AccountRoleRepository;
import com.project01.identityservice.repository.RoleRepository;
import com.project01.identityservice.services.AuthService;
import com.project01.identityservice.services.JwtService;
import com.project01.identityservice.services.NewDeviceDetector;
import com.project01.identityservice.services.RefreshTokenService;
import com.project01.identityservice.utils.OtpUtil;
import com.project01.identityservice.utils.RedisKeyUtil;
import com.project01.identityservice.utils.UserAgentParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final long OTP_TTL_MINUTES = 10;
    private static final long PENDING_PROFILE_TTL_HOURS = 24;
    private static final int OTP_MAX_ATTEMPTS = 5;
    private static final long OTP_RESEND_COOLDOWN_SECONDS = 60;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final NewDeviceDetector newDeviceDetector;
    private final IdentityProducer identityProducer;
    private final RedisService redisService;

    @Override
    @Transactional
    public RegisterResponse createAccount(RegisterDTO registerDTO) {
        String username = registerDTO.getUsername().trim();
        String email = registerDTO.getEmail().trim().toLowerCase(Locale.ROOT);

        if (accountRepository.existsByUsername(username)) {
            throw new BusinessException(ErrorCode.USER_EXITSED);
        }
        if (accountRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_EXITSED);
        }

        AccountEntity account = accountRepository.save(AccountEntity.builder()
                .username(username)
                .email(email)
                .phone(registerDTO.getPhone())
                .passwordHash(passwordEncoder.encode(registerDTO.getPassword()))
                .status(AccountStatus.PENDING)
                .emailVerified(false)
                .build());

        // Người tự đăng ký luôn là CLIENT, không nhận role từ request
        RoleEntity clientRole = roleRepository.findByCode(Role.CLIENT.name())
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOTFOUND));
        accountRoleRepository.save(AccountRoleEntity.builder()
                .accountId(account.getId())
                .roleId(clientRole.getId())
                .build());

        // Hồ sơ tạm KHÔNG chứa mật khẩu (Redis lưu dạng JSON đọc được)
        RegisterDTO pendingProfile = RegisterDTO.builder()
                .userid(account.getId())
                .username(username)
                .email(email)
                .firstName(registerDTO.getFirstName())
                .lastName(registerDTO.getLastName())
                .phone(registerDTO.getPhone())
                .address(registerDTO.getAddress())
                .build();
        redisService.saveDataOnTime(RedisKeyUtil.generateKey(RedisKeyUtil.PENDING_PROFILE, account.getId()),
                pendingProfile, PENDING_PROFILE_TTL_HOURS, TimeUnit.HOURS);

        sendOtp(account);
        return RegisterResponse.builder().userId(account.getId()).email(email).build();
    }

    @Override
    @Transactional
    public void verifyAccount(String userId, String code) {
        AccountEntity account = accountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOTFOUND));
        if (account.getStatus() != AccountStatus.PENDING) {
            log.info("Tài khoản của userId : {} thì đã xác thực",userId);
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_VERIFIED);
        }

        String otpKey = RedisKeyUtil.generateKey(RedisKeyUtil.OTP_CODE, userId);
        String attemptKey = RedisKeyUtil.generateKey(RedisKeyUtil.OTP_ATTEMPT, userId);
        String storedOtp = redisService.getData(otpKey);
        if (storedOtp == null) {
            log.info("Mã Opt : {} thì đã hết hạn",otpKey);
            throw new BusinessException(ErrorCode.OTP_EXPIRED);
        }
        if (!Objects.equals(storedOtp, code)) {
            handleWrongOtp(otpKey, attemptKey);
        }

        String pendingProfileKey = RedisKeyUtil.generateKey(RedisKeyUtil.PENDING_PROFILE, userId);
        RegisterDTO pendingProfile = redisService.getData(pendingProfileKey);
        if (pendingProfile == null) {
            throw new BusinessException(ErrorCode.REGISTRATION_EXPIRED);
        }

        account.setStatus(AccountStatus.ACTIVE);
        account.setEmailVerified(true);
        accountRepository.save(account);

        // profile-service tạo hồ sơ qua Kafka. Event chỉ được gửi sau khi transaction này commit
        // (AccountVerifiedRelay), profile-service đang tắt cũng không làm hỏng việc xác thực
        eventPublisher.publishEvent(AccountVerifiedEvent.builder()
                .userId(account.getId())
                .username(account.getUsername())
                .email(account.getEmail())
                .firstName(pendingProfile.getFirstName())
                .lastName(pendingProfile.getLastName())
                .phone(pendingProfile.getPhone())
                .address(pendingProfile.getAddress())   // cái eventPublisher này là gi mà co thể gửi qua kafka
                .build());
        redisService.deleteData(otpKey);
        redisService.deleteData(attemptKey);
        redisService.deleteData(pendingProfileKey);
    }

    @Override
    public void resendOtp(String email) {
        AccountEntity account = accountRepository
                .findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .orElse(null);
        if (account == null) {
            log.info("Tài khoản có email {} không tồn tại",email);
            return;
        }
        if (account.getStatus() != AccountStatus.PENDING) {
            throw new BusinessException(ErrorCode.ACCOUNT_ALREADY_VERIFIED);
        }
        if (!redisService.existsKey(RedisKeyUtil.generateKey(RedisKeyUtil.PENDING_PROFILE, account.getId()))) {
            throw new BusinessException(ErrorCode.REGISTRATION_EXPIRED);
        }
        String cooldownKey = RedisKeyUtil.generateKey(RedisKeyUtil.OTP_RESEND_COOLDOWN, account.getId());
        if (redisService.existsKey(cooldownKey)) {
            throw new BusinessException(ErrorCode.OTP_RESEND_TOO_SOON);
        }
        sendOtp(account);
    }

    @Override
    @Transactional
    public AuthenticationResponse login(AuthenticationRequest request, ClientInfo clientInfo) {
        String login = request.getUsername().trim();
        AccountEntity account = accountRepository.findByUsernameOrEmail(login, login.toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        ensureCanLogin(account);

        // Kiểm tra TRƯỚC khi lưu phiên mới, nếu không phiên vừa tạo sẽ khiến thiết bị luôn "đã từng đăng nhập"
        boolean newDevice = newDeviceDetector.isNewDevice(account.getId(), clientInfo.userAgent());

        account.setLastLoginAt(LocalDateTime.now());
        accountRepository.save(account);

        RefreshTokenService.IssuedToken refreshToken =
                refreshTokenService.issue(account.getId(), clientInfo.userAgent(), clientInfo.ipAddress());
        if (newDevice) {
            publishNewDeviceLogin(account, refreshToken.sessionId(), clientInfo);
        }
        return buildTokenResponse(account, refreshToken);
    }

    /**
     * Đổi refresh token lấy cặp token mới. Role được đọc lại từ DB, nên role vừa được cấp
     * (vd được duyệt thành SELLER) sẽ có trong access token mới mà không cần đăng nhập lại.
     */
    @Override
    @Transactional
    public AuthenticationResponse refreshToken(RefreshTokenRequest request, ClientInfo clientInfo) {
        RefreshTokenService.Rotation rotation = refreshTokenService.rotate(
                request.getRefreshToken(), clientInfo.userAgent(), clientInfo.ipAddress());
        AccountEntity account = accountRepository.findById(rotation.accountId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID));
        ensureCanLogin(account);
        return buildTokenResponse(account, rotation.newToken());
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        try {
            SignedJWT signedJWT = jwtService.verifyAccessToken(request.getAccessToken());
            jwtService.blacklist(signedJWT);
        } catch (BusinessException e) {
            log.debug("Logout with invalid or expired access token, nothing to blacklist");
        }
        if (StringUtils.hasText(request.getRefreshToken())) {
            refreshTokenService.revoke(request.getRefreshToken());
        }
    }

    @Override
    public IntrospectTokenResponse introspect(String accessToken) {
        return IntrospectTokenResponse.builder()
                .isValid(jwtService.introspectToken(accessToken))
                .build();
    }

    private void sendOtp(AccountEntity account) {
        String otpCode = OtpUtil.generateOtpCode();
        redisService.saveDataOnTime(RedisKeyUtil.generateKey(RedisKeyUtil.OTP_CODE, account.getId()),
                otpCode, OTP_TTL_MINUTES, TimeUnit.MINUTES);
        redisService.deleteData(RedisKeyUtil.generateKey(RedisKeyUtil.OTP_ATTEMPT, account.getId())); //chỗ này sao lại phải xóa
        redisService.saveDataOnTime(RedisKeyUtil.generateKey(RedisKeyUtil.OTP_RESEND_COOLDOWN, account.getId()),
                "1", OTP_RESEND_COOLDOWN_SECONDS, TimeUnit.SECONDS);

        identityProducer.sendVerifyAccount(VerifyAccountRequest.builder()
                .token(otpCode)
                .linkUrl(frontendUrl)
                .userId(account.getId())
                .email(account.getEmail())
                .build());
    }

    private void handleWrongOtp(String otpKey, String attemptKey) {
        Integer attempts = redisService.getData(attemptKey);
        int newAttempts = attempts == null ? 1 : attempts + 1;
        if (newAttempts >= OTP_MAX_ATTEMPTS) {
            redisService.deleteData(otpKey);
            redisService.deleteData(attemptKey);
            throw new BusinessException(ErrorCode.OTP_TOO_MANY_ATTEMPTS);
        }
        redisService.saveDataOnTime(attemptKey, newAttempts, OTP_TTL_MINUTES, TimeUnit.MINUTES);
        throw new BusinessException(ErrorCode.OTP_INVALID);
    }

    private void ensureCanLogin(AccountEntity account) {
        if (account.getStatus() == AccountStatus.LOCKED) {
            throw new BusinessException(ErrorCode.USER_LOCKED);
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.USER_DISABLE);
        }
    }

    /**
     * Phát event cảnh báo (gửi Kafka sau khi commit). Email gửi tới chủ tài khoản thay vì tới "thiết bị đầu tiên",
     * vì thiết bị đó có thể đã tắt, đã đăng xuất hoặc chính là máy của kẻ gian.
     */
    private void publishNewDeviceLogin(AccountEntity account, Long sessionId, ClientInfo clientInfo) {
        UserAgentParser.DeviceInfo device = UserAgentParser.parse(clientInfo.userAgent());
        int otherActiveSessions = (int) refreshTokenService.findActiveSessions(account.getId()).stream()
                .filter(session -> !session.getId().equals(sessionId))
                .count();
        eventPublisher.publishEvent(NewDeviceLoginEvent.builder()
                .userId(account.getId())
                .username(account.getUsername())
                .email(account.getEmail())
                .sessionId(sessionId)
                .browser(device.browser())
                .os(device.os())
                .ipAddress(clientInfo.ipAddress())
                .loginAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")))
                .otherActiveSessions(otherActiveSessions)
                .manageUrl(frontendUrl + "/account")
                .build());
    }

    private AuthenticationResponse buildTokenResponse(AccountEntity account, RefreshTokenService.IssuedToken refreshToken) {
        List<String> roles = roleRepository.findActiveRoleCodesByAccountId(account.getId());
        CustomUserDetail userDetail = new CustomUserDetail(account.getId(), account.getUsername(),
                account.getPasswordHash(), roles, true, true);
        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateAccessToken(userDetail, refreshToken.sessionId()))
                .refreshToken(refreshToken.rawToken())
                .expiresIn(jwtService.getAccessTokenTtlSeconds())
                .authenticated(true)
                .build();
    }
}
