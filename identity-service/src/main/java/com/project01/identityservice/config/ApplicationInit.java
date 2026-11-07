package com.project01.identityservice.config;

import com.project01.common.security.jwt.Role;
import com.project01.identityservice.entity.AccountEntity;
import com.project01.identityservice.entity.AccountRoleEntity;
import com.project01.identityservice.enums.AccountStatus;
import com.project01.identityservice.repository.AccountRepository;
import com.project01.identityservice.repository.AccountRoleRepository;
import com.project01.identityservice.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
public class ApplicationInit {

    @Bean
    ApplicationRunner applicationRunner(AccountRepository accountRepository,
                                        RoleRepository roleRepository,
                                        AccountRoleRepository accountRoleRepository,
                                        PasswordEncoder passwordEncoder) {
        return args -> {
            if (accountRepository.existsByUsername("admin")) {
                return;
            }
            var adminRole = roleRepository.findByCode(Role.ADMIN.name());
            if (adminRole.isEmpty()) {
                log.warn("Chưa có role ADMIN trong idt_role, hãy chạy database/mysql/02_seed_master_data.sql");
                return;
            }

            AccountEntity admin = accountRepository.save(AccountEntity.builder()
                    .username("admin")
                    .email("admin@ecommerce.local")
                    .passwordHash(passwordEncoder.encode("12345678"))
                    .status(AccountStatus.ACTIVE)
                    .emailVerified(true)
                    .build());
            accountRoleRepository.save(AccountRoleEntity.builder()
                    .accountId(admin.getId())
                    .roleId(adminRole.get().getId())
                    .build());

            log.info("please change password admin,default password admin id admin");
        };
    }
}
