package com.project01.identityservice.services.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.project01.common.core.exception.BusinessException;
import com.project01.common.core.services.RedisService;
import com.project01.common.security.jwt.CustomUserDetail;
import com.project01.identityservice.exception.ErrorCode;
import com.project01.identityservice.services.JwtService;
import com.project01.identityservice.utils.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private static final String ISSUER = "identity-service";
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_SCOPE = "scope";
    /** id phiên (= id dòng idt_refresh_token), để biết access token thuộc phiên nào */
    public static final String CLAIM_SESSION_ID = "sid";

    @Value("${jwt.access-token-expiration:1800}")
    private long accessTokenExpiration;

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final RedisService redisService;

    @Override
    public String generateAccessToken(CustomUserDetail customUserDetail, Long sessionId) {
        Instant now = Instant.now();
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(customUserDetail.getUsername())
                .issuer(ISSUER)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plus(accessTokenExpiration, ChronoUnit.SECONDS)))
                .jwtID(UUID.randomUUID().toString())
                .claim(CLAIM_USER_ID, customUserDetail.getUserId())
                .claim(CLAIM_SCOPE, String.join(" ", customUserDetail.getRoles()))
                .claim(CLAIM_SESSION_ID, sessionId)
                .build();
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS512), new Payload(jwtClaimsSet.toJSONObject()));
        try {
            jwsObject.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new IllegalStateException("Cannot sign JWT", e);
        }
        return jwsObject.serialize();
    }

    @Override
    public long getAccessTokenTtlSeconds() {
        return accessTokenExpiration;
    }

    @Override
    public SignedJWT verifyAccessToken(String token) {
        if(!StringUtils.hasText(token)){
            log.info("Token is empty !!!");
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            boolean signatureValid = signedJWT.verify(new MACVerifier(secretKey));
            Date expireTime = claims.getExpirationTime();
            if (!signatureValid || expireTime == null || !expireTime.after(new Date())) {
                log.debug("JWT invalid or expired, jti={}", claims.getJWTID());
                throw new BusinessException(ErrorCode.INVALID_TOKEN);
            }
            if (redisService.existsKey(RedisKeyUtil.generateKey(RedisKeyUtil.JWT_BLACKLIST, claims.getJWTID()))) {
                log.debug("JWT is blacklisted, jti={}", claims.getJWTID());
                throw new BusinessException(ErrorCode.INVALID_TOKEN);
            }
            return signedJWT;
        } catch (ParseException | JOSEException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, e);
        }
    }

    @Override
    public boolean introspectToken(String token) {
        try {
            verifyAccessToken(token);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    @Override
    public void blacklist(SignedJWT signedJWT) {
        try {
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            long remainingMillis = claims.getExpirationTime().getTime() - System.currentTimeMillis();
            if (remainingMillis <= 0) {
                return;
            }
            redisService.saveDataOnTime(RedisKeyUtil.generateKey(RedisKeyUtil.JWT_BLACKLIST, claims.getJWTID()),
                    claims.getSubject(), remainingMillis, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, e);
        }
    }
}
