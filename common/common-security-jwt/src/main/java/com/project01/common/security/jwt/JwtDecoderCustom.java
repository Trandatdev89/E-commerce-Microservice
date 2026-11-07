package com.project01.common.security.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

public class JwtDecoderCustom implements JwtDecoder {

    private final String secretKey;
    private NimbusJwtDecoder nimbusJwtDecoder;

    public JwtDecoderCustom(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKey key = new SecretKeySpec(secretKey.getBytes(), 0, secretKey.length(), JWSAlgorithm.HS512.toString());
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(key)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        return nimbusJwtDecoder.decode(token);
    }
}
