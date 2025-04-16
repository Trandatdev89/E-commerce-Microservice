package com.project01.identityservice.controller;

import com.project01.identityservice.dto.response.AccountDTO;
import com.project01.identityservice.dto.response.ApiResponse;
import com.project01.identityservice.entity.AccountEntity;
import com.project01.identityservice.repository.AccountRepository;
import com.project01.identityservice.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping(value = "/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "API lấy ra tất cả tài khoản",
            description = "Dùng để lấy ra tất cả các tài khoản nhưng chỉ role admin mới có quyền")
    public ApiResponse<List<AccountDTO>> getAllAccount() {
        return ApiResponse.<List<AccountDTO>>builder()
                .code(200)
                .message("OK")
                .data(accountService.getAllAccount())
                .build();
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "API lấy ra tài khoản theo ID",
            description = "Dùng để lấy ra lấy ra tài khoản theo ID")
    public ApiResponse<AccountDTO> getAccountById(@PathVariable String id) {
        return ApiResponse.<AccountDTO>builder()
                .code(200)
                .message("OK")
                .data(accountService.getAccountById(id))
                .build();
    }


}
