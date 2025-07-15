package com.project01.identityservice.services.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.project01.identityservice.entity.AccountEntity;
import com.project01.identityservice.entity.InValidTokenEntity;
import com.project01.identityservice.exception.custom.AppException;
import com.project01.identityservice.exception.custom.ErrorCode;
import com.project01.identityservice.repository.AccountRepository;
import com.project01.identityservice.repository.InValidTokenRepository;
import com.project01.identityservice.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final AccountRepository accountRepository;
    private final InValidTokenRepository invalidTokenRepository;

    @Override
    public String generateAccessToken(AccountEntity accountEntity,boolean isRefresh) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(accountEntity.getUsername())
                .issuer(accountEntity.getId())
                .issueTime(new Date())
                .expirationTime(isRefresh ?
                        (new Date(Instant.now().plus(expiration+518400, ChronoUnit.SECONDS).toEpochMilli()))
                        : (new Date(Instant.now().plus(expiration, ChronoUnit.SECONDS).toEpochMilli())))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", accountEntity.getAuthorities().stream().map(item->item.getAuthority().toString()).collect(Collectors.joining(" ")))
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
        invalidTokenRepository.save(InValidTokenEntity.builder()
                .jwtId(verified.getJWTClaimsSet().getJWTID())
                .expiryTime(verified.getJWTClaimsSet().getExpirationTime())
                .account(accountEntity)
                .build());
        var refreshToken = generateAccessToken(accountEntity,true);
        return refreshToken;
    }

    @Override
    public SignedJWT verifyToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier jwsVerifier = new MACVerifier(secretKey);
        boolean verified = signedJWT.verify(jwsVerifier);
        log.info("JWT verified: " + verified);
        AccountEntity accountEntity = accountRepository
                .findByUsername(signedJWT.getJWTClaimsSet().getSubject())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOTFOUND));
        Date expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if(!(verified &&expireTime.after(new Date()))) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        if(invalidTokenRepository.existsByJwtId(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        return signedJWT;
    }

    @Override
    public boolean introspectToken(String token){
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
