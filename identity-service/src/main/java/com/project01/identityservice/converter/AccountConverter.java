package com.project01.identityservice.converter;

import com.project01.identityservice.dto.response.AccountDTO;
import com.project01.identityservice.entity.AccountEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountConverter {

    public AccountDTO toAccountDTO(AccountEntity accountEntity, List<String> roles) {
        return AccountDTO.builder()
                .id(accountEntity.getId())
                .username(accountEntity.getUsername())
                .email(accountEntity.getEmail())
                .phone(accountEntity.getPhone())
                .status(accountEntity.getStatus().name())
                .emailVerified(accountEntity.getEmailVerified())
                .lastLoginAt(accountEntity.getLastLoginAt())
                .roles(roles)
                .build();
    }
}
