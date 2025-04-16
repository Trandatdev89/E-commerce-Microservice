package com.project01.profileservice.repository;

import com.project01.profileservice.entity.ProfileEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends MongoRepository<ProfileEntity, String>{
    Optional<ProfileEntity> findByUserid(String id);
}
