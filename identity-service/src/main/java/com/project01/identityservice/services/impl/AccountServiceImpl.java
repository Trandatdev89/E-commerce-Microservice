package com.project01.identityservice.services.impl;

import com.project01.identityservice.converter.AccountConverter;
import com.project01.identityservice.dto.response.AccountDTO;
import com.project01.identityservice.entity.AccountEntity;
import com.project01.identityservice.exception.custom.AppException;
import com.project01.identityservice.exception.custom.ErrorCode;
import com.project01.identityservice.repository.AccountRepository;
import com.project01.identityservice.services.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountConverter accountConverter;

    @Override
    public List<AccountDTO> getAllAccount() {
        List<AccountEntity> accountEntities = accountRepository.findAll();
        List<AccountDTO> accountDTOS = new ArrayList<>();
        for (AccountEntity accountEntity : accountEntities) {
            accountDTOS.add(accountConverter.toAccountDTO(accountEntity));
        }
        return accountDTOS;
    }

    @Override
    public AccountDTO getAccountById(String id) {
        AccountEntity accountEntities = accountRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.USER_NOTFOUND));
        return accountConverter.toAccountDTO(accountEntities);
    }

    @Override
    public List<AccountDTO> searchAccountByUsername(String username) {
        List<AccountEntity> accountEntities= accountRepository.findAllByUsernameLike(username);
        List<AccountDTO> accountDTOS = new ArrayList<>();
        for (AccountEntity accountEntity : accountEntities) {
            accountDTOS.add(accountConverter.toAccountDTO(accountEntity));
        }
        return accountDTOS;
    }
}
