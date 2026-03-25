# Event-Driven Order System

## Overview

This project demonstrates a microservices-based event-driven system using:

- Java 21
- Spring Boot
- Kafka
- PostgreSQL
- ELK Stack (Logging)

## Services

- Order Service
- Payment Service
- Notification Service

## Architecture

Event-driven communication via Kafka topics:

- order.created
- payment.completed

## Run Infra

```bash
docker-compose -f infra/docker-compose.yml up -d

./scripts/init-topics.sh

mvn clean install

cd order-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run


---

Logging

Kibana:
http://localhost:5601

Index:
microservices-logs

Next Improvements
Outbox Pattern
Monitoring (Prometheus + Grafana)
Distributed tracing

# 🧱 5) ROOT POM (MULTI-MODULE)

## 📁 `pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.burak</groupId>
    <artifactId>event-driven-order-system</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <modules>
        <module>common</module>
        <module>order-service</module>
        <module>payment-service</module>
        <module>notification-service</module>
    </modules>

    <properties>
        <java.version>21</java.version>
    </properties>

</project>