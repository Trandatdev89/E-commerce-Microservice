package com.project01.orderservice.service.impl;

import com.project01.common.core.exception.BusinessException;
import com.project01.event.dto.OrderConfirm;
import com.project01.orderservice.convertor.OrderConvertor;
import com.project01.orderservice.dto.payment.PaymentDTO;
import com.project01.orderservice.dto.request.OrderRequest;
import com.project01.orderservice.dto.request.PurchaseRequest;
import com.project01.orderservice.dto.response.OrderResponse;
import com.project01.orderservice.dto.response.PurchaseResponse;
import com.project01.orderservice.entity.Order;
import com.project01.orderservice.entity.OrderStatusHistory;
import com.project01.orderservice.enums.OrderStatus;
import com.project01.orderservice.exception.ErrorCode;
import com.project01.orderservice.kafka.OrderProducer;
import com.project01.orderservice.repository.CancelReasonRepository;
import com.project01.orderservice.repository.OrderItemRepository;
import com.project01.orderservice.repository.OrderRepository;
import com.project01.orderservice.repository.OrderStatusHistoryRepository;
import com.project01.orderservice.repository.httpClient.*;
import com.project01.orderservice.service.OrderService;
import com.project01.orderservice.utils.ReferenceOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final String DEFAULT_CANCEL_REASON = "CUSTOMER_CHANGED_MIND";

    private final AccountClient accountClient;
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final CancelReasonRepository cancelReasonRepository;
    private final OrderConvertor orderConvertor;
    private final ProfileClient profileClient;
    private final PaymentClient paymentClient;
    private final OrderProducer orderProducer;
    private final CartClient cartClient;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        //Kiem tra khach hang ton tai:
        accountClient.getAccount(orderRequest.getCustomerId()).getData();

        //Lay ra profile cua khach hang bao gom email:
        var profileResponse = profileClient.getProfileByUserId(orderRequest.getCustomerId()).getData();

        //Chot gia tung SKU tu product-service, khong tin gia client gui len:
        List<PurchaseResponse> purchases = productClient.purchaseProduct(orderRequest.getPurchaseRequests()).getData();
        BigDecimal subtotal = purchases.stream()
                .map(PurchaseResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //Luu don hang, chi tiet don va lich su trang thai:
        Order order = orderRepository.save(orderConvertor.toOrder(generateOrderCode(), orderRequest, subtotal));
        orderItemRepository.saveAll(purchases.stream()
                .map(purchase -> orderConvertor.toOrderItem(order.getId(), purchase))
                .toList());
        orderStatusHistoryRepository.save(OrderStatusHistory.builder()
                .orderId(order.getId())
                .toStatus(OrderStatus.PENDING)
                .build());

        //Thuc hien thanh toan :
        paymentClient.createPayment(PaymentDTO.builder()
                .orderId(order.getId())
                .orderReference(order.getOrderCode())
                .amount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .customerId(order.getCustomerId())
                .customer(profileResponse)
                .build());

        //Xoa san pham trong gio hang sau khi dat hang thanh cong:
        List<Long> skuIds = orderRequest.getPurchaseRequests().stream().map(PurchaseRequest::getSkuId).toList();
        cartClient.deleteItemFromCart(skuIds, orderRequest.getCustomerId());

        orderProducer.sendOrderConfirm(OrderConfirm.builder()
                .orderReference(order.getOrderCode())
                .paymentMethod(order.getPaymentMethod())
                .products(purchases)
                .totalAmount(order.getTotalAmount())
                .customer(profileResponse)
                .build());

        return orderConvertor.toOrderResponse(order, orderItemRepository.findAllByOrderId(order.getId()));
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByPlacedAtDesc().stream()
                .map(orderConvertor::toOrderResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> getOrderByCustomerId(String id) {
        return orderRepository.findAllByCustomerIdOrderByPlacedAtDesc(id).stream()
                .map(orderConvertor::toOrderResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderByReferenceOrder(String referenceOrder) {
        Order order = orderRepository.findByOrderCode(referenceOrder)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFERENCE_NOTFOUND));
        return orderConvertor.toOrderResponse(order, orderItemRepository.findAllByOrderId(order.getId()));
    }

    /** Hủy đơn thay cho xóa: giữ lại dữ liệu để đối soát, ghi lịch sử trạng thái. */
    @Override
    @Transactional
    public void cancelOrders(List<String> orderCodes, String reasonCode) {
        String reason = reasonCode == null ? DEFAULT_CANCEL_REASON : reasonCode;
        if (!cancelReasonRepository.existsById(reason)) {
            throw new BusinessException(ErrorCode.CANCEL_REASON_NOTFOUND);
        }
        List<Order> orders = orderRepository.findAllByOrderCodeIn(orderCodes);
        for (Order order : orders) {
            if (order.getStatus().isFinal() || order.getStatus() == OrderStatus.SHIPPING) {
                throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
            }
            OrderStatus fromStatus = order.getStatus();
            order.setStatus(OrderStatus.CANCELLED);
            order.setCancelReasonCode(reason);
            orderStatusHistoryRepository.save(OrderStatusHistory.builder()
                    .orderId(order.getId())
                    .fromStatus(fromStatus)
                    .toStatus(OrderStatus.CANCELLED)
                    .reasonCode(reason)
                    .build());
        }
        orderRepository.saveAll(orders);
    }

    private String generateOrderCode() {
        String code = ReferenceOrder.generate();
        while (orderRepository.existsByOrderCode(code)) {
            code = ReferenceOrder.generate();
        }
        return code;
    }
}
