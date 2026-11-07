package com.project01.identityservice.repository;

import com.project01.identityservice.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByCode(String code);

    /** Join thủ công idt_account_role -> idt_role bằng role_id, không qua quan hệ JPA. */
    @Query("""
            select r.code from RoleEntity r, AccountRoleEntity ar
            where ar.roleId = r.id and ar.accountId = :accountId and r.isActive = true
            """)
    List<String> findActiveRoleCodesByAccountId(@Param("accountId") String accountId);
}
