package com.project01.orderservice.service.impl;

import com.project01.orderservice.convertor.OrderConvertor;
import com.project01.orderservice.dto.request.OrderRequest;
import com.project01.orderservice.dto.request.PurchaseRequest;
import com.project01.orderservice.dto.response.OrderResponse;
import com.project01.orderservice.entity.Order;
import com.project01.event.dto.OrderConfirm;
import com.project01.orderservice.exception.custom.AppException;
import com.project01.orderservice.exception.custom.ErrorCode;
import com.project01.orderservice.kafka.OrderProducer;
import com.project01.orderservice.order_detail.OrderDetailRequest;
import com.project01.orderservice.order_detail.OrderDetailService;
import com.project01.orderservice.dto.payment.PaymentDTO;
import com.project01.orderservice.repository.OrderRepository;
import com.project01.orderservice.repository.httpClient.*;
import com.project01.orderservice.service.OrderService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AccountClient accountClient;
    private final ProductClient productClient;
    private final OrderRepository orderRepository;
    private final OrderConvertor orderConvertor;
    private final OrderDetailService orderDetailService;
    private final ProfileClient profileClient;
    private final PaymentClient paymentClient;
    private final OrderProducer orderProducer;
    private final CartClient cartClient;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        //Tim ra khach thong qua id duoc gui tu don hang
        var customerResponse = accountClient.getAccount(orderRequest.getCustomerId()).getData();

        //Lay ra profile cua khach hang bao gom email dia chi :
        var profileResponse = profileClient.getProfileByUserId(orderRequest.getCustomerId()).getData();

        //Lay ra tat ca san pham trong don hang va goi den service product mua san pham:
        var productResponse = productClient.purchaseProduct(orderRequest.getPurchaseRequests()).getData();

        //Xoa san trong gio hang sau khi dat hang thanh cong:
        List<Long> productIds = orderRequest.getPurchaseRequests().stream().map(PurchaseRequest::getProductId).toList();
        cartClient.deleteItemFromCart(productIds,orderRequest.getCustomerId());

        Order order = orderRepository.save(orderConvertor.toOrder(customerResponse,orderRequest));
        //Luu chi tiet nhung don hang trong Order-Details:
        for(PurchaseRequest purchaseRequest : orderRequest.getPurchaseRequests()) {
            orderDetailService.saveOrderDetail(OrderDetailRequest.builder()
                            .order(order)
                            .productId(purchaseRequest.getProductId())
                            .quantity(purchaseRequest.getQuantity())
                    .build());
        }
        //Thuc hien thanh toan :
        var paymentRequest = PaymentDTO.builder()
                .orderId(order.getId())
                .orderReference(order.getReference())
                .amount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .customer(profileResponse)
                .build();
        Long idPayment = paymentClient.createPayment(paymentRequest);

        orderProducer.sendOrderConfirm(OrderConfirm.builder()
                .customer(profileResponse)
                        .orderReference(order.getReference())
                        .paymentMethod(orderRequest.getPaymentMethod())
                        .products(productResponse)
                        .totalAmount(orderRequest.getTotalAmount())
                        .customer(profileResponse)
                .build());

        return OrderResponse.builder()
                .id(order.getId())
                .paymentMethod(order.getPaymentMethod())
                .reference(order.getReference())
                .customerId(order.getCustomerId())
                .amount(order.getTotalAmount())
                .createdDate(order.getCreatedDate())
                .lastModifiedDate(order.getLastModifiedDate())
                .build();
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            orderResponses.add(orderConvertor.toOrderResponse(order));
        }
        return orderResponses;
    }

    @Override
    public List<OrderResponse> getOrderByCustomerId(String id) {
        List<Order> orders = orderRepository.findAllByCustomerId(id);
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            orderResponses.add(orderConvertor.toOrderResponse(order));
        }
        return orderResponses;
    }

    @Override
    public OrderResponse getOrderByReferenceOrder(String referenceOrder) {
        Order order = orderRepository.findByReference(referenceOrder).orElseThrow(() -> new AppException(ErrorCode.REFERENCE_NOTFOUND));
        return orderConvertor.toOrderResponse(order);
    }

    @Override
    public void deleteOrderByReference(List<String> reference) {
        orderRepository.deleteByReferenceIn(reference);
    }
}
