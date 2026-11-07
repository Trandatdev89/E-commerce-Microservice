package com.project01.common.core.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@AutoConfiguration
@ConditionalOnClass(name = {
        "org.springframework.data.domain.AuditorAware",
        "org.springframework.data.jpa.domain.support.AuditingEntityListener",
        "org.springframework.security.core.context.SecurityContextHolder"
})
public class JpaAuditingAutoConfiguration {

    public static final String SYSTEM_AUDITOR = "SYSTEM";

    @Bean
    @ConditionalOnMissingBean(AuditorAware.class)
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.of(SYSTEM_AUDITOR);
            }
            return Optional.of(authentication.getName());
        };
    }
}
