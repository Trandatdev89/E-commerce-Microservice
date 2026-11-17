package com.project01.profileservice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        // group-id cố định: restart service đọc tiếp từ offset cũ, không mất event
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "profile-service");
        // Chỉ tin class trong package hợp đồng event
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.project01.event.dto");
        // Lần đầu chạy thì đọc từ đầu topic, không bỏ sót tài khoản xác thực lúc service đang tắt
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // Xử lý xong từng message thì commit offset
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        // Nối trace với identity-service (traceId đi trong header message)
        factory.getContainerProperties().setObservationEnabled(true);
        // Mongo tạm lỗi: thử lại 3 lần, cách nhau 3 giây
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(3000L, 3L)));
        return factory;
    }
}
