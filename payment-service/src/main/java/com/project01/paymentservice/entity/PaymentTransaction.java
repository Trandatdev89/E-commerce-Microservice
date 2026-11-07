package com.project01.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Nhật ký giao dịch với cổng thanh toán, chỉ thêm không sửa. */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "pay_transaction")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    /** CHARGE, CALLBACK, REFUND */
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    /** SUCCESS, FAILED, PENDING */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_request")
    private String rawRequest;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_response")
    private String rawResponse;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
