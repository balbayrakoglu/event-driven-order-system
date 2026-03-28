package com.burak.order.outbox;

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
public class OutboxEventPublisher {

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.order-created}")
    private String orderCreatedTopic;

    private static final int MAX_RETRY = 3;
    private static final long RETRY_DELAY_SECONDS = 30;

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxStatus> outBoxStatuses = List.of(OutboxStatus.NEW, OutboxStatus.FAILED);
        List<OutboxEvent> events = repository.findTop50ByStatusInAndNextRetryAtLessThanEqualOrderByCreatedAtAsc(outBoxStatuses, LocalDateTime.now());

        for (OutboxEvent outboxEvent : events) {
            try {
                if ("ORDER_CREATED".equals(outboxEvent.getEventType())) {
                    OrderCreatedEvent event =
                            objectMapper.readValue(outboxEvent.getPayload(), OrderCreatedEvent.class);

                    kafkaTemplate.send(orderCreatedTopic, event.orderId().toString(), event)
                            .get();

                    outboxEvent.setStatus(OutboxStatus.SENT);
                    outboxEvent.setProcessedAt(LocalDateTime.now());
                    outboxEvent.setErrorMessage(null);

                    log.info("event=OUTBOX_PUBLISHED eventType={}  aggregateId={} retry={}",
                            outboxEvent.getEventType(), outboxEvent.getAggregateId(), outboxEvent.getRetryCount());
                }

            } catch (Exception ex) {
                int newRetryCount = outboxEvent.getRetryCount() + 1;

                outboxEvent.setRetryCount(newRetryCount);
                outboxEvent.setStatus(OutboxStatus.FAILED);
                outboxEvent.setProcessedAt(LocalDateTime.now());
                outboxEvent.setErrorMessage(ex.getMessage());

                if (newRetryCount < MAX_RETRY) {
                    outboxEvent.setNextRetryAt(LocalDateTime.now().plusSeconds(RETRY_DELAY_SECONDS));
                } else {
                    outboxEvent.setNextRetryAt(LocalDateTime.MAX);
                    log.error("event=OUTBOX_MAX_RETRY_REACHED retryCount={} outboxId={} aggregateId={}",
                            newRetryCount, outboxEvent.getId(), outboxEvent.getAggregateId());
                }
            }
        }
    }
}
