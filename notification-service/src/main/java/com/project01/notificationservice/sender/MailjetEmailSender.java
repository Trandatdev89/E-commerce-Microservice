package com.project01.notificationservice.sender;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.SendContact;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TransactionalEmail;
import com.mailjet.client.transactional.response.MessageResult;
import com.mailjet.client.transactional.response.SendEmailsResponse;
import com.mailjet.client.transactional.response.SentMessageStatus;
import com.project01.notificationservice.config.MailjetProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailjetEmailSender implements EmailSender {

    private final MailjetClient mailjetClient;
    private final MailjetProperties properties;

    @Override
    public String send(String toEmail, String toName, String subject, String htmlBody, String customId) {
        TransactionalEmail message = TransactionalEmail.builder()
                .from(new SendContact(properties.fromEmail(), properties.fromName()))
                .to(new SendContact(toEmail, toName))
                .subject(subject)
                .htmlPart(htmlBody)
                .customID(customId)
                .build();
        try {
            SendEmailsResponse response = SendEmailsRequest.builder()
                    .message(message)
                    .build()
                    .sendWith(mailjetClient);
            MessageResult result = response.getMessages()[0];
            if (result.getStatus() != SentMessageStatus.SUCCESS) {
                throw new IllegalStateException("Mailjet rejected email: " + result.getStatus());
            }
            String messageId = result.getTo() != null && !result.getTo().isEmpty()
                    ? String.valueOf(result.getTo().get(0).getMessageID())
                    : null;
            log.info("Email sent, customId={}, mailjetMessageId={}", customId, messageId);
            return messageId;
        } catch (MailjetException e) {
            throw new IllegalStateException("Cannot send email via Mailjet", e);
        }
    }
}
