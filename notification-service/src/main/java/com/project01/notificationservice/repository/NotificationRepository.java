package com.project01.notificationservice.repository;

import com.project01.notificationservice.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    Optional<Notification> findByDedupKey(String dedupKey);

    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    Optional<Notification> findByIdAndUserId(String id, String userId);

    long countByUserIdAndReadFalse(String userId);
}
