package com.burak.order.event;

import com.burak.common.events.PaymentFailedEvent;
import com.burak.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentFailedConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "${app.kafka.topics.payment-failed}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(PaymentFailedEvent event) {
        log.warn("event=PAYMENT_FAILED_RECEIVED orderId={} reason={}",
                event.orderId(), event.reason());

        orderService.markAsFailed(event.orderId());
    }
}