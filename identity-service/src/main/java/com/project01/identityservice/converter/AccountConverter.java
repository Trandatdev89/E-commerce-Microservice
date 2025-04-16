package com.project01.identityservice.converter;

import com.project01.identityservice.dto.response.AccountDTO;
import com.project01.identityservice.entity.AccountEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountConverter {

    public AccountDTO toAccountDTO(AccountEntity accountEntity){
        return AccountDTO.builder()
                .id(accountEntity.getId())
                .password(accountEntity.getPassword())
                .username(accountEntity.getUsername())
                .roles(accountEntity.getRoles().stream().map(item->item.getCode().toString()).toList())
                .build();
    }
}
