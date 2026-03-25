#!/bin/bash

echo "Creating Kafka topics..."

docker exec -it kafka kafka-topics \
  --create --if-not-exists \
  --topic order.created \
  --bootstrap-server localhost:9092 \
  --partitions 3 --replication-factor 1

docker exec -it kafka kafka-topics \
  --create --if-not-exists \
  --topic payment.completed \
  --bootstrap-server localhost:9092 \
  --partitions 3 --replication-factor 1

# Dead Letter Topics
docker exec -it kafka kafka-topics \
  --create --if-not-exists \
  --topic order.created.DLT \
  --bootstrap-server localhost:9092 \
  --partitions 1 --replication-factor 1

echo "Topics created."