package com.project01.identityservice.controller;

import com.nimbusds.jose.JOSEException;
import com.project01.common.core.dto.response.ResponseData;
import com.project01.identityservice.dto.request.AuthenticationRequest;
import com.project01.identityservice.dto.request.IntrospectTokenRequest;
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
    public ResponseData<Void> createAccount(@RequestBody RegisterDTO registerDTO){
        accountService.createAccount(registerDTO);
        return new ResponseData<Void>().success();
    }

    @GetMapping(value = "/verify")
    @Operation(summary = "API xác thực người dùng kích hoạt tài khoản",
            description = "Sau khi click vào link API này sẽ được call và kích hoạt tài khoản cho người dùng với 1 token")
    public ResponseData<Void> verifyAccount(@RequestParam String userId,@RequestParam String token){
        accountService.verifyAccount(userId,token);
        return new ResponseData<Void>().success();
    }


    @PostMapping(value = "/login")
    @Operation(summary = "API đăng nhập",
            description = "Đăng nhập thành công sẽ trả ra 1 access token và trạng thái xác thực")
    public ResponseData<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest){
        return new ResponseData<AuthenticationResponse>().success(accountService.login(authenticationRequest));
    }

    @PostMapping(value = "/logout")
    @Operation(summary = "API đăng xuất",
            description = "API sẽ làm mất hiệu lực của accessToken bằng cách lưu nó xuống table InvalidToken")
    public ResponseData<Void> logout(@RequestBody IntrospectTokenRequest introspectTokenRequest){
        accountService.logout(introspectTokenRequest);
        return new ResponseData<Void>().success();
    }

    @PostMapping(value = "/refresh-token")
    @Operation(summary = "API làm mới token",
            description = "API tạo ra 1 accessToken mới sau khi token cũ hết hạn")
    public ResponseData<AuthenticationResponse> refreshToken(@RequestBody IntrospectTokenRequest introspectTokenRequest) throws ParseException, JOSEException {
        return new ResponseData<AuthenticationResponse>().success(accountService.refreshToken(introspectTokenRequest));
    }

    @PostMapping(value = "/introspect")
    @Operation(summary = "API kiểm tra trạng thái token",
            description = "Kiểm tra xem token còn hiệu lực hay không?")
    public ResponseData<IntrospectTokenResponse> introspect(@RequestBody IntrospectTokenRequest introspectTokenRequest){
        return new ResponseData<IntrospectTokenResponse>().success(accountService.introspect(introspectTokenRequest));
    }
}
