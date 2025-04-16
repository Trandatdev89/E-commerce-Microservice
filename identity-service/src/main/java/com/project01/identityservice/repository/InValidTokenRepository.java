package com.project01.identityservice.repository;

import com.project01.identityservice.entity.InValidTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InValidTokenRepository extends JpaRepository<InValidTokenEntity,String>
{
    boolean existsByJwtId(String token);
}
