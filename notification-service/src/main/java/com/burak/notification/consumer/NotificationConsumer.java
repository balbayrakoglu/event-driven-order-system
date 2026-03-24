package com.burak.notification.consumer;

import com.burak.common.events.PaymentCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    @KafkaListener(
            topics = "payment.completed",
            groupId = "notification-group"
    )
    public void consume(PaymentCompletedEvent event) {

        log.info("event=NOTIFICATION_SENT orderId= {}", event.orderId());
        // simulate email/SMS
        log.info("Sending email for orderId={}", event.orderId());
    }
}
