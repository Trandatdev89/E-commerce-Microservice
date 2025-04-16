package com.project01.profileservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private String id;
    private String userid;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
}
