package com.project01.notificationservice.entity;

import com.project01.event.dto.OrderConfirm;
import com.project01.event.dto.PaymentConfirm;
import com.project01.event.dto.VerifyAccountRequest;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document(collection = "notification")
public class Notification {
    @MongoId
    private String id;
    private NotificationType type;
    private Date notificationDate;
    private OrderConfirm orderConfirmation;
    private PaymentConfirm paymentConfirmation;
    private VerifyAccountRequest verifyAccountRequest;
}
