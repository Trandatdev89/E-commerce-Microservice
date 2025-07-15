package com.project01.identityservice.services;

import com.project01.identityservice.dto.response.AccountDTO;

import java.util.List;

public interface AccountService {
    List<AccountDTO> getAllAccount();
    AccountDTO getAccountById(String id);
    List<AccountDTO> searchAccountByUsername(String username);
}
