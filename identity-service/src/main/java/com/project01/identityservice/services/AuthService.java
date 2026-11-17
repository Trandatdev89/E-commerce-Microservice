package com.project01.identityservice.services;

import com.project01.identityservice.dto.request.AuthenticationRequest;
import com.project01.identityservice.dto.request.LogoutRequest;
import com.project01.identityservice.dto.request.RefreshTokenRequest;
import com.project01.identityservice.dto.response.AuthenticationResponse;
import com.project01.identityservice.dto.response.IntrospectTokenResponse;
import com.project01.identityservice.dto.response.RegisterDTO;
import com.project01.identityservice.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse createAccount(RegisterDTO registerDTO);

    void verifyAccount(String userId, String code);

    void resendOtp(String email);

    AuthenticationResponse login(AuthenticationRequest request, ClientInfo clientInfo);

    AuthenticationResponse refreshToken(RefreshTokenRequest request, ClientInfo clientInfo);

    void logout(LogoutRequest request);

    IntrospectTokenResponse introspect(String accessToken);

    record ClientInfo(String userAgent, String ipAddress) {
    }
}
