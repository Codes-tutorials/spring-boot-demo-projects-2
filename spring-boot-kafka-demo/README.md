# Spring Boot Kafka Demo

This project demonstrates integrating **Apache Kafka** with **Spring Boot** for producing and consuming messages. It provides a simple REST endpoint to publish messages and a consumer that logs received messages.

## Tech Stack
- Java 17+
- Spring Boot 4.0.1
- Spring for Apache Kafka

## Run
- Start the app:
  ```bash
  ./mvnw spring-boot:run
  ```
  On Windows:
  ```powershell
  .\mvnw spring-boot:run
  ```
- Default port: `8084`

## Configuration
- File: `src/main/resources/application.properties`
- Key properties:
  - `spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}`
  - `spring.kafka.consumer.group-id=demo-group`
  - `kafka.topic=demo-topic`
  - `kafka.enabled=false`

Set `kafka.enabled=true` to enable producer and consumer. Override `spring.kafka.bootstrap-servers` via env var `KAFKA_BOOTSTRAP_SERVERS`.

## REST Endpoint
- Publish a message:
  - `POST /api/kafka/publish?message=hello`
  - Example:
    ```powershell
    curl.exe -X POST "http://localhost:8084/api/kafka/publish?message=hello"
    ```
  - Response:
    - `200 OK` when message is dispatched
    - `202 Accepted` when Kafka is disabled or broker unavailable

## Components
- `KafkaProducerService`: Sends messages to the configured topic using `KafkaTemplate<String, String>`.
- `KafkaConsumer`: Listens to messages with `@KafkaListener`. Startup is controlled by `kafka.enabled`.
- `KafkaController`: Exposes the `/api/kafka/publish` endpoint.

## Enable Kafka Locally
- Ensure a Kafka broker is running at `localhost:9092`, or set:
  ```powershell
  $env:KAFKA_BOOTSTRAP_SERVERS="your-broker:9092"
  ```
- Enable producer/consumer:
  ```
  kafka.enabled=true
  ```
  in `application.properties`, or set:
  ```powershell
  $env:KAFKA_ENABLED="true"
  ```

