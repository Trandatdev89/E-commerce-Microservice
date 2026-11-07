package com.project01.orderservice.entity;

import com.project01.common.core.entity.AuditEntity;
import com.project01.orderservice.enums.OrderStatus;
import com.project01.orderservice.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Đơn hàng. Chi tiết nằm ở OrderItem (lưu order_id, không dùng @OneToMany).
 * Địa chỉ giao hàng là snapshot tại thời điểm đặt.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ord_order")
public class Order extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_code", nullable = false, length = 30)
    private String orderCode;

    /** ref idt_account.id */
    @Column(name = "customer_id", nullable = false, columnDefinition = "char(36)")
    private String customerId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "status", nullable = false, length = 30)
    private OrderStatus status;

    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "shipping_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "payment_method_code", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Column(name = "voucher_code", length = 50)
    private String voucherCode;

    @Column(name = "receiver_name", nullable = false, length = 150)
    private String receiverName;

    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    @Column(name = "province_code", nullable = false, length = 10)
    private String provinceCode;

    @Column(name = "province_name", nullable = false, length = 100)
    private String provinceName;

    @Column(name = "ward_code", nullable = false, length = 10)
    private String wardCode;

    @Column(name = "ward_name", nullable = false, length = 150)
    private String wardName;

    @Column(name = "address_detail", nullable = false)
    private String addressDetail;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "cancel_reason_code", length = 50)
    private String cancelReasonCode;

    @Column(name = "placed_at", nullable = false)
    private LocalDateTime placedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
}
