#!/bin/bash

echo "Creating topics..."

kafka-topics --create \
  --topic order.created \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1

kafka-topics --create \
  --topic order.created.DLQ \
  --bootstrap-server localhost:9092 \
  --partitions 1 \
  --replication-factor 1