package com.project01.identityservice.controller;

import com.nimbusds.jose.JOSEException;
import com.project01.identityservice.dto.request.AuthenticationRequest;
import com.project01.identityservice.dto.request.IntrospectTokenRequest;
import com.project01.identityservice.dto.response.ApiResponse;
import com.project01.identityservice.dto.response.AuthenticationResponse;
import com.project01.identityservice.dto.response.IntrospectTokenResponse;
import com.project01.identityservice.dto.response.RegisterDTO;
import com.project01.identityservice.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService accountService;

    @PostMapping(value = "/register")
    @Operation(summary = "API đăng ký tài khoản cho người dùng",
            description = "Khi đăng ký sẽ gửi về email 1 mã xác thực người dùng phải click vào link để kích hoạt tài khoản")
    public ApiResponse createAccount(@RequestBody RegisterDTO registerDTO){
        accountService.createAccount(registerDTO);
        return ApiResponse.builder()
                .message("Hãy xác thực tài khoản bằng email")
                .code(200)
                .build();
    }

    @GetMapping(value = "/verify")
    @Operation(summary = "API xác thực người dùng kích hoạt tài khoản",
            description = "Sau khi click vào link API này sẽ được call và kích hoạt tài khoản cho người dùng với 1 token")
    public ApiResponse verifyAccount(@RequestParam String userId,@RequestParam String token){
        accountService.verifyAccount(userId,token);
        return ApiResponse.builder()
                .message("Xác thuc hoàn tất .Đăng ký thành công!")
                .code(200)
                .build();
    }


    @PostMapping(value = "/login")
    @Operation(summary = "API đăng nhập",
            description = "Đăng nhập thành công sẽ trả ra 1 access token và trạng thái xác thực")
    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest){
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Success")
                .data( accountService.login(authenticationRequest))
                .code(200)
                .build();
    }

    @PostMapping(value = "/logout")
    @Operation(summary = "API đăng xuất",
            description = "API sẽ làm mất hiệu lực của accessToken bằng cách lưu nó xuống table InvalidToken")
    public ApiResponse<AuthenticationResponse> logout(@RequestBody IntrospectTokenRequest introspectTokenRequest){
        return ApiResponse.<AuthenticationResponse>builder()
                .message("logout success")
                .data( accountService.logout(introspectTokenRequest))
                .code(200)
                .build();
    }

    @PostMapping(value = "/refresh-token")
    @Operation(summary = "API làm mới token",
            description = "API tạo ra 1 accessToken mới sau khi token cũ hết hạn")
    public ApiResponse<AuthenticationResponse> refreshToken(@RequestBody IntrospectTokenRequest introspectTokenRequest) throws ParseException, JOSEException {
        return ApiResponse.<AuthenticationResponse>builder()
                .message("logout success")
                .data( accountService.refreshToken(introspectTokenRequest))
                .code(200)
                .build();
    }

    @PostMapping(value = "/introspect")
    @Operation(summary = "API kiểm tra trạng thái token",
            description = "Kiểm tra xem token còn hiệu lực hay không?")
    public ApiResponse<IntrospectTokenResponse> introspect(@RequestBody IntrospectTokenRequest introspectTokenRequest){
        return ApiResponse.<IntrospectTokenResponse>builder()
                .message("authentication success")
                .data( accountService.introspect(introspectTokenRequest))
                .code(200)
                .build();
    }
}
