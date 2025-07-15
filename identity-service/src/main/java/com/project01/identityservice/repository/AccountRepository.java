package com.project01.identityservice.repository;

import com.project01.identityservice.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity,String> {
    Optional<AccountEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<AccountEntity> findByVerifyCode(String code);
    List<AccountEntity> findAllByUsernameLike(String username);
}
