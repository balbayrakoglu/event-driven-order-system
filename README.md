# Event-Driven Order System

A production-style distributed system demonstrating Event-Driven Architecture, Outbox Pattern, Retry Handling, and Saga
Orchestration using Kafka.

---

## Overview

This project simulates a real-world order processing system built with microservices.

Services communicate asynchronously using Kafka and ensure data consistency using modern reliability patterns.

---

## Services

- order-service → handles order lifecycle
- payment-service → processes payments
- notification-service → placeholder

---

## Architecture

Event-driven architecture using Kafka.

No direct service-to-service communication.

---

## Core Patterns

### Outbox Pattern

- atomic DB + event write
- solves dual-write problem

---

### Saga Pattern

- choreography-based
- no orchestrator
- states:
    - PAYMENT_PENDING → PAID
    - PAYMENT_PENDING → FAILED

---

### Retry

- retryCount
- nextRetryAt
- fixed delay
- max retry

---

### Idempotency

- safe duplicate handling

---

## Event Flow (Sequence)

Client  
↓  
order-service  
↓  
Outbox  
↓  
Kafka  
↓  
payment-service

SUCCESS → PaymentCompletedEvent → order-service → PAID  
FAILURE → PaymentFailedEvent → order-service → FAILED

---

## Sequence Diagram (Text)

Client -> OrderService : POST /orders  
OrderService -> DB : save order  
OrderService -> Outbox : save event  
Outbox -> Kafka : publish

Kafka -> PaymentService : OrderCreatedEvent

PaymentService -> DB : save payment

alt success  
PaymentService -> Outbox : PaymentCompletedEvent  
Kafka -> OrderService : PaymentCompletedEvent  
OrderService -> DB : mark PAID  
else failure  
PaymentService -> Outbox : PaymentFailedEvent  
Kafka -> OrderService : PaymentFailedEvent  
OrderService -> DB : mark FAILED  
end

---

## API Examples

### Create Order

POST /orders

Request:
{
"product": "phone",
"amount": 800
}

Response:
{
"orderId": "uuid",
"status": "PAYMENT_PENDING"
}

---

### Get Order

GET /orders/{id}

Response:
{
"orderId": "uuid",
"status": "PAID"
}

---

## Logs

SUCCESS:
event=ORDER_CREATED  
event=PAYMENT_SUCCESS  
event=ORDER_MARKED_PAID

FAILURE:
event=PAYMENT_FAILED  
event=ORDER_MARKED_FAILED

---

## How to Run

docker compose up --build

---

## Troubleshooting

Database error:
FATAL: database does not exist  
→ create manually

Kafka issues:
→ check containers

Events not processing:
→ check outbox table

---

## Interview Explanation (IMPORTANT)

This project demonstrates:

- how to solve dual-write problem → Outbox
- how to handle failure → Saga
- how to ensure delivery → Retry
- how to handle duplicates → Idempotency

Typical explanation:

"When an order is created, we persist both order and event in same transaction.  
A background publisher sends the event to Kafka.  
Payment service processes it and emits success or failure event.  
Order service reacts accordingly.  
This ensures eventual consistency without distributed transactions."

---

## Limitations

- no DLQ
- no correlationId
- no observability
- fixed retry

---

## Purpose

Focused demonstration of core distributed system patterns.

---

## Final Note

This is a clean and complete demo project suitable for interviews and architecture discussions.
