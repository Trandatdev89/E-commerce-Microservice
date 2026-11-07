package com.project01.common.security.jwt;

import lombok.Getter;

/**
 * Mã role, trùng với idt_role.code. Dùng trong @PreAuthorize("hasRole('...')").
 */
@Getter
public enum Role {
    ADMIN, CLIENT, SELLER, SHIPPER
}
