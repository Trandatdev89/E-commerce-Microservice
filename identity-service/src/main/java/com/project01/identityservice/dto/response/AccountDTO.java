package com.project01.identityservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private String id;
    private String username;
    private String email;
    private String phone;
    private String status;
    private Boolean emailVerified;
    private LocalDateTime lastLoginAt;
    @Builder.Default
    private List<String> roles = new ArrayList<>();
    private String deviceLogin;
    private String addressLogin;

}
