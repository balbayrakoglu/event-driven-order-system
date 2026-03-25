#!/bin/bash

echo "Stopping containers..."
docker-compose -f infra/docker-compose.yml down -v

echo "Cleaning local builds..."
mvn clean

echo "Starting infra..."
docker-compose -f infra/docker-compose.yml up -d

sleep 10

echo "Initializing topics..."
./scripts/init-topics.sh

echo "Reset complete."