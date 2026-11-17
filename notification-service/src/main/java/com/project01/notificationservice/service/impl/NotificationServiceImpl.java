package com.project01.notificationservice.service.impl;

import com.project01.common.core.exception.BusinessException;
import com.project01.notificationservice.dto.NotificationCommand;
import com.project01.notificationservice.dto.response.NotificationResponse;
import com.project01.notificationservice.entity.Notification;
import com.project01.notificationservice.entity.NotificationTemplate;
import com.project01.notificationservice.enums.NotificationChannel;
import com.project01.notificationservice.enums.NotificationStatus;
import com.project01.notificationservice.exception.ErrorCode;
import com.project01.notificationservice.repository.NotificationRepository;
import com.project01.notificationservice.repository.NotificationTemplateRepository;
import com.project01.notificationservice.sender.EmailSender;
import com.project01.notificationservice.service.NotificationService;
import com.project01.notificationservice.service.TemplateRenderer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final int MAX_PAGE_SIZE = 50;

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final TemplateRenderer templateRenderer;
    private final EmailSender emailSender;

    @Override
    public void send(NotificationCommand command) {
        Notification existing = notificationRepository.findByDedupKey(command.dedupKey()).orElse(null);
        if (existing != null && existing.getStatus() == NotificationStatus.SENT) {
            log.info("Notification {} already sent, skip duplicate event", command.dedupKey());
            return;
        }

        NotificationTemplate template = templateRepository.findByCodeAndActiveTrue(command.type())
                .orElseThrow(() -> new BusinessException(ErrorCode.TEMPLATE_NOTFOUND));
        String subject = templateRenderer.render(template.getSubject(), command.variables());
        String body = templateRenderer.render(template.getBody(), command.variables());

        Notification notification = existing != null ? existing : createPending(command, template, subject, body);

        try {
            String providerMessageId = emailSender.send(command.recipient(), command.recipientName(),
                    subject, body, notification.getId());
            notification.setStatus(NotificationStatus.SENT);
            notification.setProviderMessageId(providerMessageId);
            notification.setErrorMessage(null);
            notification.setSentAt(Instant.now());
            notificationRepository.save(notification);
        } catch (RuntimeException e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
            notificationRepository.save(notification);
            log.error("Send notification {} failed (attempt {})", command.dedupKey(), notification.getRetryCount(), e);
            throw e;
        }
    }

    @Override
    public Page<NotificationResponse> getMyNotifications(String userId, int page, int size) {
        PageRequest pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), MAX_PAGE_SIZE));
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).map(this::toResponse);
    }

    @Override
    public long countUnread(String userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public void markAsRead(String notificationId, String userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOTFOUND));
        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    /** Ghi PENDING trước khi gửi: nếu service chết giữa chừng vẫn còn dấu vết để kiểm tra */
    private Notification createPending(NotificationCommand command, NotificationTemplate template,
                                       String subject, String body) {
        Notification notification = Notification.builder()
                .dedupKey(command.dedupKey())
                .userId(command.userId())
                .type(command.type())
                .channel(NotificationChannel.EMAIL)
                .recipient(command.recipient())
                .subject(subject)
                .content(template.isSensitive() ? null : body)
                .referenceId(command.referenceId())
                .metadata(command.metadata())
                .status(NotificationStatus.PENDING)
                .createdAt(Instant.now())
                .build();
        try {
            return notificationRepository.save(notification);
        } catch (DuplicateKeyException e) {
            // 2 consumer xử lý cùng lúc 1 event: dùng bản ghi đã có
            return notificationRepository.findByDedupKey(command.dedupKey()).orElseThrow(() -> e);
        }
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .channel(notification.getChannel())
                .subject(notification.getSubject())
                .referenceId(notification.getReferenceId())
                .metadata(notification.getMetadata())
                .status(notification.getStatus())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .build();
    }
}
