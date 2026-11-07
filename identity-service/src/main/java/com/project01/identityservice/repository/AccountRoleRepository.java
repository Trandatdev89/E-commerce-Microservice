package com.project01.identityservice.repository;

import com.project01.identityservice.entity.AccountRoleEntity;
import com.project01.identityservice.entity.id.AccountRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRoleEntity, AccountRoleId> {
    List<AccountRoleEntity> findAllByAccountIdIn(Collection<String> accountIds);
}
