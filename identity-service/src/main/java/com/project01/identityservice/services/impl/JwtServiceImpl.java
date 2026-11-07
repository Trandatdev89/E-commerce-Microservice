package com.project01.identityservice.services.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.project01.common.core.exception.BusinessException;
import com.project01.common.core.services.RedisService;
import com.project01.common.security.jwt.CustomUserDetail;
import com.project01.identityservice.entity.AccountEntity;
import com.project01.identityservice.exception.ErrorCode;
import com.project01.identityservice.repository.AccountRepository;
import com.project01.identityservice.services.JwtService;
import com.project01.identityservice.utils.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final AccountRepository accountRepository;
    private final RedisService redisService;

    @Override
    public String generateAccessToken(CustomUserDetail customUserDetail, boolean isRefresh) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(customUserDetail.getUsername())
                .issuer(customUserDetail.getUserId())
                .issueTime(new Date())
                .expirationTime(isRefresh ?
                        (new Date(Instant.now().plus(expiration + 518400, ChronoUnit.SECONDS).toEpochMilli()))
                        : (new Date(Instant.now().plus(expiration, ChronoUnit.SECONDS).toEpochMilli())))
                .jwtID(UUID.randomUUID().toString())
                // Mã role cách nhau bởi dấu cách, vd "ADMIN CLIENT". Resource server tự thêm tiền tố ROLE_
                .claim("scope", String.join(" ", customUserDetail.getRoles()))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    @Override
    public String generateRefreshToken(String token) throws ParseException, JOSEException {
        SignedJWT verified = verifyToken(token);
        AccountEntity accountEntity = accountRepository.findByUsername(verified.getJWTClaimsSet().getSubject()).get();
        return null;
    }

    @Override
    public SignedJWT verifyToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier jwsVerifier = new MACVerifier(secretKey);
        boolean verified = signedJWT.verify(jwsVerifier);
        log.info("JWT verified: " + verified);
        Date expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (!(verified && expireTime.after(new Date()))) {
            log.info("JWT is expireTime already : {}", token);
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        String redisJwtKey = RedisKeyUtil
                .generateKey(RedisKeyUtil.JWT_BLACKLIST, signedJWT.getJWTClaimsSet().getJWTID());
        if (redisService.existsKey(redisJwtKey)) {
            log.info("JWT is blacklist already : {}", token);
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        return signedJWT;
    }

    @Override
    public boolean introspectToken(String token) {
        boolean isValid = false;
        try {
            SignedJWT signedJWT = verifyToken(token);
            isValid = true;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return isValid;
    }
}
