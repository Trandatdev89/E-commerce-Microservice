package com.project01.profileservice.consumer;

import com.project01.event.dto.AccountVerifiedEvent;
import com.project01.profileservice.dto.response.RegisterDTO;
import com.project01.profileservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Tạo hồ sơ khi tài khoản được xác thực. createProfile bỏ qua nếu hồ sơ đã có,
 * nên Kafka giao lại cùng 1 event cũng không tạo trùng.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AccountEventConsumer {

    private final ProfileService profileService;

    @KafkaListener(topics = "account-verified-topic", containerFactory = "kafkaListenerContainerFactory")
    public void onAccountVerified(AccountVerifiedEvent event) {
        profileService.createProfile(RegisterDTO.builder()
                .userid(event.getUserId())
                .username(event.getUsername())
                .email(event.getEmail())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .phone(event.getPhone())
                .address(event.getAddress())
                .build());
        log.info("Profile ensured for user {}", event.getUserId());
    }
}
