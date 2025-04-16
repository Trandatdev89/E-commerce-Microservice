package com.project01.identityservice.config;

import com.project01.identityservice.entity.AccountEntity;
import com.project01.identityservice.entity.RoleEntity;
import com.project01.identityservice.repository.AccountRepository;
import com.project01.identityservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ApplicationInit {

    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(AccountRepository accountRepository, RoleRepository roleRepository) {
        return args -> {
            List<RoleEntity> roleEntity=roleRepository.findAll();
            if(roleEntity.size()==0 || roleEntity==null){
                List<RoleEntity> roles= new ArrayList<>();
                RoleEntity role1=RoleEntity.builder()
                        .code("ADMIN")
                        .name("admin").build();
                RoleEntity role2=RoleEntity.builder()
                        .code("USER")
                        .name("khach").build();
                roles.add(role1);
                roles.add(role2);
                roleRepository.saveAll(roles);
                boolean user = accountRepository.existsByUsername("admin");
                if(user==false){
                        List<RoleEntity> role = new ArrayList<>();
                        role.add(roleRepository.findByCode("ADMIN"));
                        AccountEntity admin = AccountEntity.builder()
                                .username("admin")
                                .password(passwordEncoder.encode("admin"))
                                .roles(role)
                                .status(true)
                                .verifyCode("kjhgfdfghsskdkdkd")
                                .build();
                        accountRepository.save(admin);
                }
                else{
                    log.info("Admin Exits");
                }
            }
            log.info("please change password admin,default password admin id admin");
        };
    }
}
