package com.project01.identityservice.kafka;

import com.project01.event.dto.AccountVerifiedEvent;
import com.project01.event.dto.NewDeviceLoginEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Chỉ gửi Kafka SAU KHI transaction commit thành công.
 * Nếu gửi ngay trong transaction mà sau đó DB rollback, service khác sẽ xử lý một việc chưa từng xảy ra
 * (tạo hồ sơ cho tài khoản chưa kích hoạt, gửi cảnh báo cho lần đăng nhập không thành công...).
 * <p>
 * Vẫn còn rủi ro nhỏ: DB đã commit nhưng gửi Kafka lỗi thì mất event. Muốn chắc chắn tuyệt đối
 * thì dùng Outbox pattern (ghi event vào bảng outbox cùng transaction, job đọc và gửi sau).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class IdentityEventRelay {

    private final IdentityProducer identityProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAccountVerified(AccountVerifiedEvent event) {
        identityProducer.sendAccountVerified(event);
        log.info("Published AccountVerifiedEvent for user {}", event.getUserId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNewDeviceLogin(NewDeviceLoginEvent event) {
        identityProducer.sendNewDeviceLogin(event);
        log.info("Published NewDeviceLoginEvent for user {}, device {} / {}", event.getUserId(), event.getBrowser(), event.getOs());
    }
}
