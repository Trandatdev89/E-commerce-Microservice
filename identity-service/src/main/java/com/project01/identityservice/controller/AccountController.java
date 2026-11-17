package com.project01.identityservice.controller;

import com.project01.common.core.dto.response.ResponseData;
import com.project01.identityservice.dto.response.AccountDTO;
import com.project01.identityservice.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/account")
@CrossOrigin(value = "*")
public class AccountController {

    private final AccountService accountService;

    @GetMapping(value = "/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "API lấy ra tất cả tài khoản",
            description = "Dùng để lấy ra tất cả các tài khoản nhưng chỉ role admin mới có quyền")
    public ResponseData<List<AccountDTO>> getAllAccount() {
        return new ResponseData<List<AccountDTO>>().success(accountService.getAllAccount());
    }

    @GetMapping(value = "/username")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    @Operation(summary = "API lấy ra tất cả tài khoản",
            description = "Dùng để lấy ra tất cả các tài khoản nhưng chỉ role admin mới có quyền")
    public ResponseData<List<AccountDTO>> searchAccountByUsername(@RequestParam String username) {
        return new ResponseData<List<AccountDTO>>().success(accountService.searchAccountByUsername(username));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    @Operation(summary = "API lấy ra tài khoản theo ID",
            description = "Dùng để lấy ra lấy ra tài khoản theo ID")
    public ResponseData<AccountDTO> getAccountById(@PathVariable String id) {
        return new ResponseData<AccountDTO>().success(accountService.getAccountById(id));
    }

}
