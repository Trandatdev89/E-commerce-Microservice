package com.project01.identityservice.services.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.project01.identityservice.dto.request.AuthenticationRequest;
import com.project01.identityservice.dto.request.IntrospectTokenRequest;
import com.project01.event.dto.VerifyAccountRequest;
import com.project01.identityservice.dto.response.AuthenticationResponse;
import com.project01.identityservice.dto.response.IntrospectTokenResponse;
import com.project01.identityservice.dto.response.RegisterDTO;
import com.project01.identityservice.entity.AccountEntity;
import com.project01.identityservice.entity.InValidTokenEntity;
import com.project01.identityservice.exception.custom.AppException;
import com.project01.identityservice.exception.custom.ErrorCode;
import com.project01.identityservice.kafka.IdentityProducer;
import com.project01.identityservice.repository.AccountRepository;
import com.project01.identityservice.repository.InValidTokenRepository;
import com.project01.identityservice.repository.RoleRepository;
import com.project01.identityservice.repository.httpClient.ProfileClient;
import com.project01.identityservice.services.AuthService;
import com.project01.identityservice.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${api.gate-way}")
    private String urlGateWay;

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ProfileClient profileClient;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtService jwtService;
    private final InValidTokenRepository inValidTokenRepository;
    private final IdentityProducer identityProducer;

    @Override
    public void createAccount(RegisterDTO registerDTO) {
       if(accountRepository.existsByUsername(registerDTO.getUsername())) {
           throw new AppException(ErrorCode.USER_EXITSED);
       }
        AccountEntity accountEntity = AccountEntity.builder()
                .username(registerDTO.getUsername())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .roles(List.of(roleRepository.findByCode("USER")))
                .status(false)
                .build();
        AccountEntity accountEntityCreated = accountRepository.save(accountEntity);
        registerDTO.setUserid(accountEntityCreated.getId());
        profileClient.createProfile(registerDTO);
        identityProducer.sendVerifyAccount(VerifyAccountRequest.builder()
                        .token(UUID.randomUUID().toString())
                        .linkUrl(urlGateWay)
                        .userId(accountEntityCreated.getId())
                        .email(registerDTO.getEmail())
                .build());
    }


    @Override
    public void verifyAccount(String userId,String code) {
        AccountEntity account = accountRepository.findById(userId).orElseThrow(()->new AppException(ErrorCode.USER_NOTFOUND));
        if(account.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now())){
             throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        account.setVerifyCode(code);
        account.setStatus(true);
        accountRepository.save(account);
    }



    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
         AccountEntity accountEntity = accountRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow(()-> new AppException(ErrorCode.USER_EXITSED));
         if(!passwordEncoder.matches(authenticationRequest.getPassword(), accountEntity.getPassword()) && accountEntity.isStatus()) {
             throw new AppException(ErrorCode.PASSWORD_WRONG);
         }
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),authenticationRequest.getPassword());
        AuthenticationManager authenticationManager=authenticationManagerBuilder.getObject();
        Authentication authentication=authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        return AuthenticationResponse.builder()
                .token(jwtService.generateAccessToken(accountEntity,false))
                .authenticated(true)
                .build();
    }

    @Override
    public AuthenticationResponse logout(IntrospectTokenRequest introspectTokenRequest) {
        SignedJWT verified = null;
        AccountEntity accountEntity = null;
        String jwtId=null;
        Date expiryTime = null;
        try {
            verified = jwtService.verifyToken(introspectTokenRequest.getToken());
            accountEntity=accountRepository.findByUsername(verified.getJWTClaimsSet().getSubject()).get();
            jwtId=verified.getJWTClaimsSet().getJWTID();
            expiryTime=verified.getJWTClaimsSet().getExpirationTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        inValidTokenRepository.save(InValidTokenEntity.builder()
                .jwtId(jwtId)
                .expiryTime(expiryTime)
                .account(accountEntity)
                .build());
        return AuthenticationResponse.builder()
                .token(introspectTokenRequest.getToken())
                .authenticated(false)
                .build();
    }

    @Override
    public IntrospectTokenResponse introspect(IntrospectTokenRequest introspectTokenRequest) {
        return IntrospectTokenResponse.builder()
                .isValid(jwtService.introspectToken(introspectTokenRequest.getToken()))
                .build();
    }

    @Override
    public AuthenticationResponse refreshToken(IntrospectTokenRequest introspectTokenRequest) throws ParseException, JOSEException {
        return AuthenticationResponse.builder()
                .token(jwtService.generateRefreshToken(introspectTokenRequest.getToken()))
                .authenticated(true)
                .build();
    }

}
