package com.project01.notificationservice.repository;

import com.project01.notificationservice.entity.NotificationTemplate;
import com.project01.notificationservice.enums.NotificationType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NotificationTemplateRepository extends MongoRepository<NotificationTemplate, String> {
    Optional<NotificationTemplate> findByCodeAndActiveTrue(NotificationType code);

    boolean existsByCode(NotificationType code);
}
