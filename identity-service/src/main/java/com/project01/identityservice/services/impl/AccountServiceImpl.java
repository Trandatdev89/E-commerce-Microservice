package com.project01.identityservice.services.impl;

import com.project01.common.core.exception.BusinessException;
import com.project01.identityservice.converter.AccountConverter;
import com.project01.identityservice.dto.response.AccountDTO;
import com.project01.identityservice.entity.AccountEntity;
import com.project01.identityservice.entity.AccountRoleEntity;
import com.project01.identityservice.entity.RoleEntity;
import com.project01.identityservice.exception.ErrorCode;
import com.project01.identityservice.repository.AccountRepository;
import com.project01.identityservice.repository.AccountRoleRepository;
import com.project01.identityservice.repository.RoleRepository;
import com.project01.identityservice.services.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;
    private final RoleRepository roleRepository;
    private final AccountConverter accountConverter;

    @Override
    public List<AccountDTO> getAllAccount() {
        return toAccountDTOs(accountRepository.findAll());
    }

    @Override
    public AccountDTO getAccountById(String id) {
        AccountEntity accountEntity = accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOTFOUND));
        return accountConverter.toAccountDTO(accountEntity, roleRepository.findActiveRoleCodesByAccountId(id));
    }

    @Override
    public List<AccountDTO> searchAccountByUsername(String username) {
        return toAccountDTOs(accountRepository.findAllByUsernameContaining(username));
    }

    /** Lấy role của cả danh sách tài khoản bằng 2 query, tránh N+1. */
    private List<AccountDTO> toAccountDTOs(List<AccountEntity> accounts) {
        if (accounts.isEmpty()) {
            return List.of();
        }
        List<AccountRoleEntity> accountRoles = accountRoleRepository
                .findAllByAccountIdIn(accounts.stream().map(AccountEntity::getId).toList());
        Map<Long, String> roleCodeById = roleRepository
                .findAllById(accountRoles.stream().map(AccountRoleEntity::getRoleId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(RoleEntity::getId, RoleEntity::getCode));
        Map<String, List<String>> rolesByAccountId = accountRoles.stream()
                .collect(Collectors.groupingBy(AccountRoleEntity::getAccountId,
                        Collectors.mapping(ar -> roleCodeById.get(ar.getRoleId()), Collectors.toList())));

        return accounts.stream()
                .map(account -> accountConverter.toAccountDTO(account,
                        rolesByAccountId.getOrDefault(account.getId(), List.of())))
                .toList();
    }
}
