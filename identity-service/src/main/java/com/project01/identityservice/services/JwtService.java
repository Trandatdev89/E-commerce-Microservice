package com.project01.identityservice.services;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.project01.common.security.jwt.CustomUserDetail;
import com.project01.identityservice.entity.AccountEntity;

import java.text.ParseException;

public interface JwtService {
    String generateAccessToken(CustomUserDetail customUserDetail, boolean isRefresh);
    String generateRefreshToken(String token) throws ParseException, JOSEException;
    SignedJWT verifyToken(String token) throws ParseException, JOSEException;
    boolean introspectToken(String token);
}
