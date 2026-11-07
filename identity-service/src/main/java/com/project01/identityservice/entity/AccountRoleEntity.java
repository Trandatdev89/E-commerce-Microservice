package com.project01.identityservice.entity;

import com.project01.identityservice.entity.id.AccountRoleId;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(AccountRoleId.class)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "idt_account_role")
public class AccountRoleEntity {

    @Id
    @Column(name = "account_id", columnDefinition = "char(36)")
    private String accountId;

    @Id
    @Column(name = "role_id")
    private Long roleId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;
}
