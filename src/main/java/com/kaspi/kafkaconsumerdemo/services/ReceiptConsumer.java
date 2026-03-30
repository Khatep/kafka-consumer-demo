package com.kaspi.kafkaconsumerdemo.services;

import com.kaspi.kafkaconsumerdemo.domain.entities.Receipt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptConsumer {

    private final EmailNotificationService emailNotificationService;

    @KafkaListener(topics = "receipts", groupId = "group-1")
    public void listenReceiptTopic(ConsumerRecord<String, Receipt> receiptConsumerRecord) {
        Receipt receipt = receiptConsumerRecord.value();
        CompletableFuture
                .supplyAsync(emailNotificationService.sendReceiptToEmail(receipt))
                .thenAccept(emailNotificationService::save)
                .exceptionally(ex -> {
                    log.error("Error sending receipt to email", ex);
                    return null;
                });
    }
}
