package com.burak.paymentservice.outbox;

import com.burak.common.events.OrderCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentOutboxPublisher {

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.order-created}")
    private String orderCreatedTopic;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events = repository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.NEW);

        for (OutboxEvent outboxEvent : events) {
            try {
                if ("ORDER_CREATED".equals(outboxEvent.getEventType())) {
                    OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(outboxEvent.getPayload(), OrderCreatedEvent.class);

                    kafkaTemplate.send(orderCreatedTopic, orderCreatedEvent.orderId().toString(), orderCreatedEvent).get();

                    outboxEvent.setStatus(OutboxStatus.SENT);
                    outboxEvent.setProcessedAt(LocalDateTime.now());
                    outboxEvent.setErrorMessage(null);

                    log.info("event=OUTBOX_PUBLISHED eventType={} aggregateId={}",
                            outboxEvent.getEventType(), outboxEvent.getAggregateId());

                }
            } catch (Exception ex) {
                outboxEvent.setStatus(OutboxStatus.FAILED);
                outboxEvent.setProcessedAt(LocalDateTime.now());
                outboxEvent.setErrorMessage(ex.getMessage());
            }
        }
    }
}
