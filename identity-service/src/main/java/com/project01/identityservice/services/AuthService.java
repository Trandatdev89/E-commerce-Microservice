package com.project01.identityservice.services;

import com.nimbusds.jose.JOSEException;
import com.project01.identityservice.dto.request.AuthenticationRequest;
import com.project01.identityservice.dto.request.IntrospectTokenRequest;
import com.project01.identityservice.dto.response.AuthenticationResponse;
import com.project01.identityservice.dto.response.IntrospectTokenResponse;
import com.project01.identityservice.dto.response.RegisterDTO;

import java.text.ParseException;

public interface AuthService {
    void createAccount(RegisterDTO registerDTO);
    AuthenticationResponse login(AuthenticationRequest authenticationRequest);
    void logout(IntrospectTokenRequest introspectTokenRequest);
    IntrospectTokenResponse introspect(IntrospectTokenRequest introspectTokenRequest);
    AuthenticationResponse refreshToken(IntrospectTokenRequest introspectTokenRequest) throws ParseException, JOSEException;
    void verifyAccount(String userId,String code);
}
