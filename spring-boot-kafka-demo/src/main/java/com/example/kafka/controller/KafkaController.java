package com.example.kafka.controller;

import com.example.kafka.service.KafkaProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {

  private final KafkaProducerService producer;

  public KafkaController(KafkaProducerService producer) {
    this.producer = producer;
  }

  @PostMapping("/publish")
  public ResponseEntity<String> publish(@RequestParam String message) {
    boolean dispatched = producer.send(message);
    if (!dispatched) {
      return ResponseEntity.accepted().body("Kafka disabled or unavailable; message not sent");
    }
    return ResponseEntity.ok("Message dispatched to Kafka");
  }
}

