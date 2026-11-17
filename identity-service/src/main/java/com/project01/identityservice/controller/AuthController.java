package com.project01.identityservice.controller;

import com.project01.common.core.dto.response.ResponseData;
import com.project01.identityservice.dto.request.AuthenticationRequest;
import com.project01.identityservice.dto.request.IntrospectTokenRequest;
import com.project01.identityservice.dto.request.LogoutRequest;
import com.project01.identityservice.dto.request.RefreshTokenRequest;
import com.project01.identityservice.dto.request.ResendOtpRequest;
import com.project01.identityservice.dto.response.AuthenticationResponse;
import com.project01.identityservice.dto.response.IntrospectTokenResponse;
import com.project01.identityservice.dto.response.RegisterDTO;
import com.project01.identityservice.dto.response.RegisterResponse;
import com.project01.identityservice.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/register")
    @Operation(summary = "API đăng ký tài khoản cho người dùng",
            description = "Tạo tài khoản CLIENT ở trạng thái chờ xác thực và gửi mã OTP (kèm link xác thực) về email")
    public ResponseData<RegisterResponse> createAccount(@Valid @RequestBody RegisterDTO registerDTO) {
        return new ResponseData<RegisterResponse>().success(authService.createAccount(registerDTO));
    }

    @GetMapping(value = "/verify")
    @Operation(summary = "API xác thực tài khoản bằng OTP",
            description = "Link trong email gọi API này. Sai OTP 5 lần thì mã bị hủy, phải yêu cầu gửi lại")
    public ResponseData<Void> verifyAccount(@RequestParam(name = "userId") String userId,
                                            @RequestParam(name="token") String token) {
        authService.verifyAccount(userId, token);
        return new ResponseData<Void>().success();
    }

    @PostMapping(value = "/resend-otp")
    @Operation(summary = "API gửi lại mã OTP",
            description = "Dùng khi OTP hết hạn (10 phút) hoặc nhập sai quá nhiều lần. Mỗi lần gửi cách nhau tối thiểu 60 giây")
    public ResponseData<Void> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        authService.resendOtp(request.getEmail());
        return new ResponseData<Void>().success();
    }

    @PostMapping(value = "/login")
    @Operation(summary = "API đăng nhập",
            description = "Đăng nhập bằng username hoặc email. Trả về access token " +
                    "và refresh token")
    public ResponseData<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest request,
                                                      HttpServletRequest httpRequest) {
        return new ResponseData<AuthenticationResponse>()
                .success(authService.login(request, clientInfo(httpRequest)));
    }

    @PostMapping(value = "/refresh-token")
    @Operation(summary = "API làm mới token",
            description = "Đổi refresh token lấy cặp token mới. Refresh token cũ bị thu hồi ngay (rotation)")
    public ResponseData<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request,
                                                             HttpServletRequest httpRequest) {
        return new ResponseData<AuthenticationResponse>().success(authService.refreshToken(request, clientInfo(httpRequest)));
    }

    @PostMapping(value = "/logout")
    @Operation(summary = "API đăng xuất",
            description = "Đưa access token vào blacklist tới khi hết hạn và thu hồi refresh token")
    public ResponseData<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return new ResponseData<Void>().success();
    }

    @PostMapping(value = "/introspect")
    @Operation(summary = "API kiểm tra trạng thái token",
            description = "Gateway gọi API này để kiểm tra access token còn hiệu lực hay không")
    public ResponseData<IntrospectTokenResponse> introspect(@RequestBody IntrospectTokenRequest request) {
        return new ResponseData<IntrospectTokenResponse>().success(authService.introspect(request.getToken()));
    }

    private AuthService.ClientInfo clientInfo(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String ip = StringUtils.hasText(forwardedFor) ? forwardedFor.split(",")[0].trim() : request.getRemoteAddr();
        return new AuthService.ClientInfo(request.getHeader(HttpHeaders.USER_AGENT), ip);
    }
}
