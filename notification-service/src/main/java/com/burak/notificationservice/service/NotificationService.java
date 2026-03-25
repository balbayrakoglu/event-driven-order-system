package com.burak.notificationservice.service;

import com.burak.common.events.PaymentCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void sendNotification(PaymentCompletedEvent event) {

        log.info("event=NOTIFICATION_PROCESS_START eventId={} orderId={}",
                event.eventId(), event.orderId());

        log.info("event=EMAIL_SENT orderId={}", event.orderId());

        log.info("event=SMS_SENT orderId={}", event.orderId());
    }
}
