package com.project01.notificationservice.services;

import com.project01.event.dto.OrderConfirm;
import com.project01.event.dto.PaymentConfirm;
import com.project01.event.dto.VerifyAccountRequest;

public interface EmailService {
    void sendEmailForPayment(PaymentConfirm paymentConfirm);
    void sendEmailForOrder(OrderConfirm orderConfirm);
    void verifyAccount(VerifyAccountRequest verifyAccountRequest);
}
