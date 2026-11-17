package com.project01.identityservice.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IntrospectTokenResponse {
    @NotBlank(message = "Token không được để trống")
    private boolean isValid;
}
