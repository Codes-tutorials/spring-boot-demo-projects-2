package com.example.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
  private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

  @KafkaListener(
      topics = "${kafka.topic:demo-topic}",
      groupId = "${spring.kafka.consumer.group-id:demo-group}",
      autoStartup = "${kafka.enabled:false}"
  )
  public void consume(String message) {
    log.info("Consumed message: {}", message);
  }
}

