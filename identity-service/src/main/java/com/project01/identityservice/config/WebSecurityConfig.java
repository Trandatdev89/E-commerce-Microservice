package com.project01.identityservice.config;

import com.project01.common.core.exception.BusinessException;
import com.project01.common.security.jwt.CustomUserDetail;
import com.project01.identityservice.entity.AccountEntity;
import com.project01.identityservice.enums.AccountStatus;
import com.project01.identityservice.exception.ErrorCode;
import com.project01.identityservice.repository.AccountRepository;
import com.project01.identityservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            AccountEntity accountInDB = accountRepository.findByUsername(username)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOTFOUND));

            return new CustomUserDetail(
                    accountInDB.getId(),
                    accountInDB.getUsername(),
                    accountInDB.getPasswordHash(),
                    roleRepository.findActiveRoleCodesByAccountId(accountInDB.getId()),
                    accountInDB.getStatus() != AccountStatus.LOCKED,
                    accountInDB.getStatus() == AccountStatus.ACTIVE);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
