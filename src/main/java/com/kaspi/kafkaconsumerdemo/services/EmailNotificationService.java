package com.kaspi.kafkaconsumerdemo.services;

import com.kaspi.kafkaconsumerdemo.domain.entities.EmailNotification;
import com.kaspi.kafkaconsumerdemo.domain.entities.Receipt;
import com.kaspi.kafkaconsumerdemo.domain.enums.EmailStatus;
import com.kaspi.kafkaconsumerdemo.repositories.EmailNotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final EmailNotificationRepository emailNotificationRepository;

    public Supplier<EmailNotification> sendReceiptToEmail(Receipt receipt) {
        return () -> {
            EmailNotification notification = EmailNotification.builder()
                    .receiptNumber(receipt.getReceiptNumber())
                    .recipientEmail(receipt.getClientEmail())
                    .subject("Ваш чек #" + receipt.getReceiptNumber())
                    .status(EmailStatus.SENT)
                    .build();
            try {
                var message = mailSender.createMimeMessage();
                var helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(receipt.getClientEmail());
                helper.setSubject("Ваш чек #" + receipt.getReceiptNumber());
                helper.setText(buildHtmlReceipt(receipt), true); // true = html

                mailSender.send(message);
                log.info("Receipt email sent to {}", receipt.getClientEmail());

            } catch (Exception e) {
                log.error("Failed to send email to {}", receipt.getClientEmail(), e);
                notification.setStatus(EmailStatus.FAILED);
                notification.setErrorMessage(e.getMessage());
            }
            return notification;
        };
    }

    @Transactional
    public void save(EmailNotification emailNotification) {
        emailNotificationRepository.save(emailNotification);
    }

    private String buildHtmlReceipt(Receipt receipt) {
        return """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }
                        .container { max-width: 600px; margin: auto; background: #fff;
                                     border-radius: 8px; padding: 30px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
                        .header { text-align: center; border-bottom: 2px solid #e8192c; padding-bottom: 16px; }
                        .header h1 { color: #e8192c; margin: 0; font-size: 24px; }
                        .section { margin: 20px 0; }
                        .row { display: flex; justify-content: space-between; padding: 6px 0;
                               border-bottom: 1px solid #f0f0f0; }
                        .label { color: #888; font-size: 14px; }
                        .value { font-weight: bold; font-size: 14px; }
                        .total-row { display: flex; justify-content: space-between; padding: 10px 0; }
                        .total-label { font-size: 16px; font-weight: bold; }
                        .total-value { font-size: 18px; font-weight: bold; color: #e8192c; }
                        .footer { text-align: center; margin-top: 30px; color: #aaa; font-size: 12px; }
                    </style>
                </head>
                <body>
                <div class="container">
                    <div class="header">
                        <h1>Kaspi Pay</h1>
                        <p>Электронный чек</p>
                    </div>

                    <div class="section">
                        <div class="row">
                            <span class="label">Номер чека</span>
                            <span class="value">%s</span>
                        </div>
                        <div class="row">
                            <span class="label">Описание</span>
                            <span class="value">%s</span>
                        </div>
                        <div class="row">
                            <span class="label">Продавец</span>
                            <span class="value">%s</span>
                        </div>
                        <div class="row">
                            <span class="label">БИН продавца</span>
                            <span class="value">%s</span>
                        </div>
                        <div class="row">
                            <span class="label">Терминал</span>
                            <span class="value">%s</span>
                        </div>
                        <div class="row">
                            <span class="label">Метод оплаты</span>
                            <span class="value">%s</span>
                        </div>
                    </div>

                    <div class="section">
                        <div class="row">
                            <span class="label">Сумма</span>
                            <span class="value">%s %s</span>
                        </div>
                        <div class="row">
                            <span class="label">Скидка</span>
                            <span class="value">-%s %s</span>
                        </div>
                        <div class="row">
                            <span class="label">НДС</span>
                            <span class="value">%s %s</span>
                        </div>
                        <div class="total-row">
                            <span class="total-label">Итого</span>
                            <span class="total-value">%s %s</span>
                        </div>
                    </div>

                    <div class="footer">
                        <p>Спасибо за использование Kaspi Pay!</p>
                        <p>Это письмо сформировано автоматически, отвечать на него не нужно.</p>
                    </div>
                </div>
                </body>
                </html>
                """.formatted(
                receipt.getReceiptNumber(),
                receipt.getDescription(),
                receipt.getMerchantName(),
                receipt.getMerchantBin(),
                receipt.getTerminalId(),
                receipt.getPaymentMethod(),
                receipt.getAmount(), receipt.getCurrency(),
                receipt.getDiscountAmount(), receipt.getCurrency(),
                receipt.getTaxAmount(), receipt.getCurrency(),
                receipt.getTotalAmount(), receipt.getCurrency()
        );
    }
}