package com.project01.notificationservice.services.impl;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.SendContact;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TransactionalEmail;
import com.mailjet.client.transactional.response.SendEmailsResponse;
import com.project01.event.dto.OrderConfirm;
import com.project01.event.dto.PaymentConfirm;
import com.project01.event.dto.VerifyAccountRequest;
import com.project01.notificationservice.entity.Notification;
import com.project01.notificationservice.entity.NotificationType;
import com.project01.notificationservice.repository.NotificationRepository;
import com.project01.notificationservice.services.EmailService;
import com.project01.notificationservice.utils.MailjetUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final MailjetUtils mailjetUtils;
    private final NotificationRepository notificationRepository;

    @Override
    public void sendEmailForPayment(PaymentConfirm paymentConfirm) {

        //Su dung mailJet de gui mail cho khach hang
        mailjetUtils.sendMailWithMailjet(paymentConfirm.getCustomerEmail(),
                paymentConfirm.getCustomerFirstname()+" "+paymentConfirm.getCustomerLastname(),
                "Thanh toán đơn hàng thành công",
                "<h1> Tong tien: "+paymentConfirm.getAmount()+" Thanh toan bang "+paymentConfirm.getPaymentMethod()+" Mã đơn hàng "+paymentConfirm.getOrderReference()+"</h1>",
                paymentConfirm.getOrderReference());

        //Save Notification to DB
        notificationRepository.save(Notification.builder()
                        .notificationDate(new Date())
                        .type(NotificationType.PAYMENT_CONFIRMATION)
                        .paymentConfirmation(paymentConfirm)
                .build());
    }

    @Override
    public void sendEmailForOrder(OrderConfirm orderConfirm) {

        //Su dung mailJet de gui mail cho khach hang
        mailjetUtils.sendMailWithMailjet(orderConfirm.getCustomer().getEmail(),
                orderConfirm.getCustomer().getFirstName()+" "+orderConfirm.getCustomer().getLastName(),
                "Đặt hàng thành công",
                "<h1> Mã đơn hàng: "+orderConfirm.getOrderReference()+" Thanh toan bang "+orderConfirm.getPaymentMethod()+" Mã đơn hàng "+orderConfirm.getOrderReference()+" So san pham"+orderConfirm.getProducts().toString()+" Tong tien"+orderConfirm.getTotalAmount()+"</h1>",
                orderConfirm.getOrderReference());

        //Save Notification to DB
        notificationRepository.save(Notification.builder()
                .notificationDate(new Date())
                .type(NotificationType.ORDER_CONFIRMATION)
                .orderConfirmation(orderConfirm)
                .build());
    }

    @Override
    public void verifyAccount(VerifyAccountRequest verifyAccountRequest) {
        String httpUrlSend = verifyAccountRequest.getLinkUrl()+"/verify?userId="+verifyAccountRequest.getUserId()+"&token="+verifyAccountRequest.getToken();
        mailjetUtils.sendMailWithMailjet(verifyAccountRequest.getEmail(),
                "No Reply",
                "Verify Account",
                "Click to link of Verify Account: "+httpUrlSend,
                verifyAccountRequest.getToken());

        notificationRepository.save(Notification.builder()
                .notificationDate(new Date())
                .type(NotificationType.VERIFICATION_ACCOUNT)
                .verifyAccountRequest(verifyAccountRequest)
                .build());
    }
}
