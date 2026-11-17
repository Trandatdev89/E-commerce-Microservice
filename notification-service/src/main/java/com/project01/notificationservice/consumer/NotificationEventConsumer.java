package com.project01.notificationservice.consumer;

import com.project01.event.dto.NewDeviceLoginEvent;
import com.project01.event.dto.OrderConfirm;
import com.project01.event.dto.PaymentConfirm;
import com.project01.event.dto.PurchaseResponse;
import com.project01.event.dto.VerifyAccountRequest;
import com.project01.notificationservice.dto.NotificationCommand;
import com.project01.notificationservice.enums.NotificationType;
import com.project01.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Nhận event từ các service khác và chuyển thành NotificationCommand.
 * Chỉ làm việc "dịch" event -> lệnh gửi; logic gửi / chống trùng nằm ở NotificationService.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private static final Locale VI = Locale.forLanguageTag("vi-VN");

    private final NotificationService notificationService;

    @KafkaListener(topics = "identity-topic", containerFactory = "kafkaListenerContainerFactory")
    public void onVerifyAccount(VerifyAccountRequest event) {
        String verifyUrl = event.getLinkUrl() + "/verify?userId=" + event.getUserId() + "&token=" + event.getToken();
        notificationService.send(NotificationCommand.builder()
                .type(NotificationType.VERIFY_ACCOUNT)
                .dedupKey(NotificationType.VERIFY_ACCOUNT + ":" + event.getUserId() + ":" + sha256(event.getToken()).substring(0, 16))
                .userId(event.getUserId())
                .recipient(event.getEmail())
                .recipientName(event.getEmail())
                .referenceId(event.getUserId())
                .variables(mapOf("otp", event.getToken(), "verifyUrl", verifyUrl))
                .metadata(mapOf())
                .build());
    }

    @KafkaListener(topics = "order-topic", containerFactory = "kafkaListenerContainerFactory")
    public void onOrderConfirmed(OrderConfirm event) {
        String customerName = fullName(event.getCustomer().getFirstName(), event.getCustomer().getLastName());
        notificationService.send(NotificationCommand.builder()
                .type(NotificationType.ORDER_CONFIRMATION)
                .dedupKey(NotificationType.ORDER_CONFIRMATION + ":" + event.getOrderReference())
                .userId(event.getCustomerId())
                .recipient(event.getCustomer().getEmail())
                .recipientName(customerName)
                .referenceId(event.getOrderReference())
                .variables(mapOf(
                        "customerName", customerName,
                        "orderCode", event.getOrderReference(),
                        "paymentMethod", String.valueOf(event.getPaymentMethod()),
                        "totalAmount", formatMoney(event.getTotalAmount()),
                        "itemsHtml", itemsTableHtml(event.getProducts())))
                .metadata(mapOf(
                        "totalAmount", event.getTotalAmount(),
                        "paymentMethod", String.valueOf(event.getPaymentMethod()),
                        "itemCount", event.getProducts() == null ? 0 : event.getProducts().size()))
                .build());
    }

    @KafkaListener(topics = "payment-topic", containerFactory = "kafkaListenerContainerFactory")
    public void onPaymentCreated(PaymentConfirm event) {
        String customerName = fullName(event.getCustomerFirstname(), event.getCustomerLastname());
        notificationService.send(NotificationCommand.builder()
                .type(NotificationType.PAYMENT_CONFIRMATION)
                .dedupKey(NotificationType.PAYMENT_CONFIRMATION + ":" + event.getOrderReference())
                .userId(event.getCustomerId())
                .recipient(event.getCustomerEmail())
                .recipientName(customerName)
                .referenceId(event.getOrderReference())
                .variables(mapOf(
                        "customerName", customerName,
                        "orderCode", event.getOrderReference(),
                        "paymentMethod", String.valueOf(event.getPaymentMethod()),
                        "amount", formatMoney(event.getAmount())))
                .metadata(mapOf(
                        "amount", event.getAmount(),
                        "paymentMethod", String.valueOf(event.getPaymentMethod())))
                .build());
    }

    @KafkaListener(topics = "new-device-login-topic", containerFactory = "kafkaListenerContainerFactory")
    public void onNewDeviceLogin(NewDeviceLoginEvent event) {
        notificationService.send(NotificationCommand.builder()
                .type(NotificationType.NEW_DEVICE_LOGIN)
                // Mỗi phiên đăng nhập chỉ cảnh báo 1 lần
                .dedupKey(NotificationType.NEW_DEVICE_LOGIN + ":" + event.getSessionId())
                .userId(event.getUserId())
                .recipient(event.getEmail())
                .recipientName(event.getUsername())
                .referenceId(String.valueOf(event.getSessionId()))
                .variables(mapOf(
                        "username", event.getUsername(),
                        "browser", event.getBrowser(),
                        "os", event.getOs(),
                        "ipAddress", event.getIpAddress(),
                        "loginAt", event.getLoginAt(),
                        "otherActiveSessions", event.getOtherActiveSessions(),
                        "manageUrl", event.getManageUrl()))
                .metadata(mapOf(
                        "device", event.getBrowser() + " trên " + event.getOs(),
                        "ipAddress", event.getIpAddress(),
                        "loginAt", event.getLoginAt()))
                .build());
    }

    /** Như Map.of nhưng bỏ qua giá trị null (Map.of ném NullPointerException nếu event thiếu field) */
    private static Map<String, Object> mapOf(Object... keyValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < keyValues.length; i += 2) {
            if (keyValues[i + 1] != null) {
                map.put((String) keyValues[i], keyValues[i + 1]);
            }
        }
        return map;
    }

    private static String itemsTableHtml(List<PurchaseResponse> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        StringBuilder html = new StringBuilder("<table border=\"1\" cellpadding=\"6\" cellspacing=\"0\">"
                + "<tr><th>Sản phẩm</th><th>Phân loại</th><th>SL</th><th>Đơn giá</th><th>Thành tiền</th></tr>");
        for (PurchaseResponse item : items) {
            html.append("<tr><td>").append(HtmlUtils.htmlEscape(String.valueOf(item.getProductName())))
                    .append("</td><td>").append(HtmlUtils.htmlEscape(String.valueOf(item.getSkuName())))
                    .append("</td><td>").append(item.getQuantity())
                    .append("</td><td>").append(formatMoney(item.getUnitPrice()))
                    .append("</td><td>").append(formatMoney(item.getLineTotal()))
                    .append("</td></tr>");
        }
        return html.append("</table>").toString();
    }

    private static String formatMoney(BigDecimal amount) {
        return amount == null ? "" : NumberFormat.getCurrencyInstance(VI).format(amount);
    }

    private static String fullName(String firstName, String lastName) {
        return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
    }

    private static String sha256(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(String.valueOf(value).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
