package com.project01.identityservice.services.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.project01.common.core.exception.BusinessException;
import com.project01.common.core.services.RedisService;
import com.project01.common.security.jwt.CustomUserDetail;
import com.project01.event.dto.VerifyAccountRequest;
import com.project01.identityservice.dto.request.AuthenticationRequest;
import com.project01.identityservice.dto.request.IntrospectTokenRequest;
import com.project01.identityservice.dto.response.AuthenticationResponse;
import com.project01.identityservice.dto.response.IntrospectTokenResponse;
import com.project01.identityservice.dto.response.RegisterDTO;
import com.project01.common.security.jwt.Role;
import com.project01.identityservice.entity.AccountEntity;
import com.project01.identityservice.entity.AccountRoleEntity;
import com.project01.identityservice.enums.AccountStatus;
import com.project01.identityservice.entity.RoleEntity;
import com.project01.identityservice.exception.ErrorCode;
import com.project01.identityservice.kafka.IdentityProducer;
import com.project01.identityservice.repository.AccountRepository;
import com.project01.identityservice.repository.AccountRoleRepository;
import com.project01.identityservice.repository.RoleRepository;
import com.project01.identityservice.repository.httpClient.ProfileClient;
import com.project01.identityservice.services.AuthService;
import com.project01.identityservice.services.JwtService;
import com.project01.identityservice.utils.OtpUtil;
import com.project01.identityservice.utils.RedisKeyUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    @Value("${api.gate-way}")
    private String urlGateWay;

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileClient profileClient;
    private final JwtService jwtService;
    private final IdentityProducer identityProducer;
    private final RedisService redisService;
    private final UserDetailsService userDetailsService;

    @Override
    public void createAccount(RegisterDTO registerDTO) {

        if (accountRepository.existsByUsername(registerDTO.getUsername())) {
            throw new BusinessException(ErrorCode.USER_EXITSED);
        }
        if (accountRepository.existsByEmail(registerDTO.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_EXITSED);
        }

        AccountEntity accountEntity = AccountEntity.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .phone(registerDTO.getPhone())
                .passwordHash(passwordEncoder.encode(registerDTO.getPassword()))
                .status(AccountStatus.PENDING)
                .emailVerified(false)
                .build();
        AccountEntity accountEntityCreated = accountRepository.save(accountEntity);

        // Người tự đăng ký luôn là CLIENT, không nhận role từ request
        RoleEntity clientRole = roleRepository.findByCode(Role.CLIENT.name())
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOTFOUND));
        accountRoleRepository.save(AccountRoleEntity.builder()
                .accountId(accountEntityCreated.getId())
                .roleId(clientRole.getId())
                .build());
        registerDTO.setUserid(accountEntityCreated.getId());

        String otpCode = OtpUtil.generateOtpCode();

        // save time otp code
        redisService.saveDataOnTime(RedisKeyUtil.generateKey(
                        RedisKeyUtil.OTP_CODE, accountEntityCreated.getId()), otpCode,
                10, TimeUnit.MINUTES);

        //save data temp profile
        redisService.saveDataOnTime(RedisKeyUtil.generateKey(
                        RedisKeyUtil.PENDING_PROFILE, accountEntityCreated.getId()), registerDTO,
                10, TimeUnit.MINUTES);

        identityProducer.sendVerifyAccount(VerifyAccountRequest.builder()
                .token(otpCode)
                .linkUrl(urlGateWay)
                .userId(accountEntityCreated.getId())
                .email(registerDTO.getEmail())
                .build());
    }

    @Override
    public void verifyAccount(String userId, String code) {
        AccountEntity account = accountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOTFOUND));

        String keyVerifyAccount = RedisKeyUtil.generateKey(RedisKeyUtil.OTP_CODE, userId);
        String storedOtp = redisService.getData(keyVerifyAccount);

        if (!Objects.equals(storedOtp, code)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        String keyPendingProfile = RedisKeyUtil.generateKey(RedisKeyUtil.PENDING_PROFILE, userId);
        RegisterDTO registerDTO = redisService.getData(keyPendingProfile);
        if (registerDTO == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        profileClient.createProfile(registerDTO);

        account.setStatus(AccountStatus.ACTIVE);
        account.setEmailVerified(true);
        accountRepository.save(account);
        redisService.deleteData(keyVerifyAccount);
        redisService.deleteData(keyPendingProfile);
    }


    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {

        CustomUserDetail user = (CustomUserDetail) userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        if (!passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_WRONG);
        }

        if (!user.isAccountNonLocked()) {
            throw new BusinessException(ErrorCode.USER_LOCKED);
        }

        if (!user.isEnabled()) {
            throw new BusinessException(ErrorCode.USER_DISABLE);
        }

        accountRepository.findById(user.getUserId()).ifPresent(account -> {
            account.setLastLoginAt(LocalDateTime.now());
            accountRepository.save(account);
        });

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        return AuthenticationResponse.builder()
                .token(jwtService.generateAccessToken(user, false))
                .authenticated(true)
                .build();
    }

    @Override
    public void logout(IntrospectTokenRequest introspectTokenRequest) {
        SignedJWT verified = null;
        String jwtId = null;
        try {
            verified = jwtService.verifyToken(introspectTokenRequest.getToken());
            jwtId = verified.getJWTClaimsSet().getJWTID();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        redisService.saveDataOnTime(RedisKeyUtil.generateKey(RedisKeyUtil.JWT_BLACKLIST, jwtId), introspectTokenRequest.getToken(), 10, TimeUnit.MINUTES);
    }

    @Override
    public IntrospectTokenResponse introspect(IntrospectTokenRequest introspectTokenRequest) {
        return IntrospectTokenResponse.builder()
                .isValid(jwtService.introspectToken(introspectTokenRequest.getToken()))
                .build();
    }

    @Override
    public AuthenticationResponse refreshToken(IntrospectTokenRequest introspectTokenRequest) throws ParseException, JOSEException {
        return AuthenticationResponse.builder()
                .token(jwtService.generateRefreshToken(introspectTokenRequest.getToken()))
                .authenticated(true)
                .build();
    }

}
