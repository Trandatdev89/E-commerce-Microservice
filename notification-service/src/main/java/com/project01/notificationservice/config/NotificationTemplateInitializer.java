package com.project01.notificationservice.config;

import com.project01.notificationservice.entity.NotificationTemplate;
import com.project01.notificationservice.enums.NotificationChannel;
import com.project01.notificationservice.enums.NotificationType;
import com.project01.notificationservice.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Tạo mẫu mặc định nếu chưa có. Mẫu đã tồn tại (có thể admin đã sửa) thì giữ nguyên.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationTemplateInitializer implements ApplicationRunner {

    private final NotificationTemplateRepository templateRepository;

    @Override
    public void run(ApplicationArguments args) {
        defaultTemplates().stream()
                .filter(template -> !templateRepository.existsByCode(template.getCode()))
                .forEach(template -> {
                    templateRepository.save(template);
                    log.info("Created default notification template {}", template.getCode());
                });
    }

    private List<NotificationTemplate> defaultTemplates() {
        Instant now = Instant.now();
        return List.of(
                NotificationTemplate.builder()
                        .code(NotificationType.VERIFY_ACCOUNT)
                        .channel(NotificationChannel.EMAIL)
                        .subject("Xác thực tài khoản")
                        .body("<p>Xin chào,</p>"
                                + "<p>Mã xác thực tài khoản của bạn là: <b style=\"font-size:20px;letter-spacing:4px\">{{otp}}</b></p>"
                                + "<p>Hoặc bấm vào link sau để kích hoạt tài khoản: <a href=\"{{verifyUrl}}\">Xác thực tài khoản</a></p>"
                                + "<p>Mã có hiệu lực trong 10 phút. Nếu bạn không đăng ký tài khoản, hãy bỏ qua email này.</p>")
                        .sensitive(true)
                        .active(true)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                NotificationTemplate.builder()
                        .code(NotificationType.ORDER_CONFIRMATION)
                        .channel(NotificationChannel.EMAIL)
                        .subject("Đặt hàng thành công - đơn {{orderCode}}")
                        .body("<p>Xin chào {{customerName}},</p>"
                                + "<p>Đơn hàng <b>{{orderCode}}</b> của bạn đã được ghi nhận.</p>"
                                + "{{itemsHtml}}"
                                + "<p>Tổng tiền: <b>{{totalAmount}}</b><br>Phương thức thanh toán: {{paymentMethod}}</p>"
                                + "<p>Cảm ơn bạn đã mua hàng!</p>")
                        .sensitive(false)
                        .active(true)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                NotificationTemplate.builder()
                        .code(NotificationType.PAYMENT_CONFIRMATION)
                        .channel(NotificationChannel.EMAIL)
                        .subject("Thông tin thanh toán đơn {{orderCode}}")
                        .body("<p>Xin chào {{customerName}},</p>"
                                + "<p>Đơn hàng <b>{{orderCode}}</b> có số tiền cần thanh toán <b>{{amount}}</b>"
                                + " bằng phương thức {{paymentMethod}}.</p>")
                        .sensitive(false)
                        .active(true)
                        .createdAt(now)
                        .updatedAt(now)
                        .build(),
                NotificationTemplate.builder()
                        .code(NotificationType.NEW_DEVICE_LOGIN)
                        .channel(NotificationChannel.EMAIL)
                        .subject("Cảnh báo: tài khoản vừa đăng nhập trên thiết bị mới")
                        .body("<p>Xin chào {{username}},</p>"
                                + "<p>Tài khoản của bạn vừa được đăng nhập trên một thiết bị mới:</p>"
                                + "<ul><li>Thiết bị: <b>{{browser}} trên {{os}}</b></li>"
                                + "<li>Địa chỉ IP: {{ipAddress}}</li>"
                                + "<li>Thời gian: {{loginAt}}</li></ul>"
                                + "<p>Hiện có thêm {{otherActiveSessions}} thiết bị khác đang đăng nhập.</p>"
                                + "<p>Nếu đây là bạn, có thể bỏ qua email này. Nếu <b>không phải bạn</b>, hãy "
                                + "<a href=\"{{manageUrl}}\">đăng xuất thiết bị lạ</a> và đổi mật khẩu ngay.</p>")
                        .sensitive(false)
                        .active(true)
                        .createdAt(now)
                        .updatedAt(now)
                        .build());
    }
}
