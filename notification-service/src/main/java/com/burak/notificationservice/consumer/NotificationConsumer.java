package com.burak.notificationservice.consumer;

import com.burak.common.events.PaymentCompletedEvent;
import com.burak.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService service;

    @KafkaListener(
            topics = "${app.kafka.topics.payment-completed}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(PaymentCompletedEvent event) {

        log.info("event=PAYMENT_COMPLETED_RECEIVED eventId={} orderId={}",
                event.eventId(), event.orderId());

        service.sendNotification(event);
    }
}