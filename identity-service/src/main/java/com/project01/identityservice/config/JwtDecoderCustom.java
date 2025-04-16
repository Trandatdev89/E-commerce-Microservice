package com.project01.identityservice.config;

import com.nimbusds.jose.JWSAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtDecoderCustom implements JwtDecoder {

    @Value("${jwt.secretKey}")
    private String secretKey;
    private NimbusJwtDecoder nimbusJwtDecoder;
    @Override
    public Jwt decode(String token) throws JwtException {
         if(Objects.isNull(nimbusJwtDecoder)) {
             SecretKey key = new SecretKeySpec(secretKey.getBytes(),0,secretKey.length(), JWSAlgorithm.HS512.toString());
             nimbusJwtDecoder=NimbusJwtDecoder
                     .withSecretKey(key)
                     .macAlgorithm(MacAlgorithm.HS512)
                     .build();
         }
         return nimbusJwtDecoder.decode(token);
    }
}
