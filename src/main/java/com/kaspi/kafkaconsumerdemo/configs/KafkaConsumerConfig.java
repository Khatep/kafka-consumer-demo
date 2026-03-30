package com.kaspi.kafkaconsumerdemo.configs;

import com.kaspi.kafkaconsumerdemo.domain.entities.Receipt;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, Receipt> receiptConsumerFactory(ObjectMapper objectMapper) {
        Deserializer<Receipt> receiptDeserializer = (topic, data) -> {
            try {
                 return objectMapper.readValue(data, Receipt.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize Receipt", e);
            }
        };

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), receiptDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Receipt> kafkaListenerContainerFactory(
            ConsumerFactory<String, Receipt> receiptConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Receipt> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(receiptConsumerFactory);
        return factory;
    }
}