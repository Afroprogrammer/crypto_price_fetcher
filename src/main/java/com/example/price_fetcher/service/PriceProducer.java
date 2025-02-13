package com.example.price_fetcher.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PriceProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "price-updates";

    public PriceProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPriceUpdate(String priceUpdate) {
        kafkaTemplate.send(TOPIC, priceUpdate);
        System.out.println("Sent price update: " + priceUpdate);
    }
}
