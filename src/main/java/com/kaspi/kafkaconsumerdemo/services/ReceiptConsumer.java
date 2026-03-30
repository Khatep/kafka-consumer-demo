package com.kaspi.kafkaconsumerdemo.services;

import com.kaspi.kafkaconsumerdemo.domain.entities.Receipt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptConsumer {

    private final EmailNotificationService emailNotificationService;

    private final Executor taskExecutor;

    @KafkaListener(topics = "receipts", groupId = "group-1")
    public void listenReceiptTopic(ConsumerRecord<String, Receipt> receiptConsumerRecord) {
        Receipt receipt = receiptConsumerRecord.value();
        CompletableFuture
                .supplyAsync(emailNotificationService.sendReceiptToEmail(receipt), taskExecutor)
                .thenAccept(emailNotificationService::save)
                .exceptionally(ex -> {
                    log.error("Error sending receipt to email", ex);
                    return null;
                });
    }
}
