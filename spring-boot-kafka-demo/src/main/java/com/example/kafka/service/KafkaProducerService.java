package com.example.kafka.service;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class KafkaProducerService {
  private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String topic;
  private final boolean kafkaEnabled;

  public KafkaProducerService(
      KafkaTemplate<String, String> kafkaTemplate,
      @Value("${kafka.topic:demo-topic}") String topic,
      @Value("${kafka.enabled:false}") boolean kafkaEnabled
  ) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
    this.kafkaEnabled = kafkaEnabled;
  }

  public boolean send(String message) {
    if (!kafkaEnabled) {
      log.info("Kafka is disabled; skipping send for message='{}'", message);
      return false;
    }
    try {
      var result = kafkaTemplate.send(topic, message)
          .orTimeout(5, TimeUnit.SECONDS)
          .join();
      RecordMetadata metadata = result.getRecordMetadata();
      log.info("Message sent to topic='{}' partition={} offset={}",
          metadata.topic(), metadata.partition(), metadata.offset());
      return true;
    } catch (Exception e) {
      log.error("Failed to send message to Kafka: {}", e.getMessage());
      return false;
    }
  }
}
