package com.kaspi.kafkaconsumerdemo.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kaspi.kafkaconsumerdemo.domain.enums.Currency;
import com.kaspi.kafkaconsumerdemo.domain.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Receipt {

    private UUID receiptNumber;

    private String description;

    private String fileUrl;

    private String issuer;

    private Long paymentId;

    private String clientEmail;

    private BigDecimal amount;

    private BigDecimal taxAmount;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private Currency currency;

    private PaymentMethod paymentMethod;

    private String merchantId;

    private String merchantName;

    private String merchantBin;

    private String terminalId;

    private LocalDateTime issuedAt;

}