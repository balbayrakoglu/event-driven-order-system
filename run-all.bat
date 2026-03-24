@echo off

echo Starting infrastructure...
docker-compose up -d

timeout /t 10

echo Building project...
mvn clean install -DskipTests

echo Starting order-service...
start cmd /k "cd order-service && mvn spring-boot:run"

timeout /t 5

echo Starting payment-service...
start cmd /k "cd payment-service && mvn spring-boot:run"

echo SYSTEM IS UP