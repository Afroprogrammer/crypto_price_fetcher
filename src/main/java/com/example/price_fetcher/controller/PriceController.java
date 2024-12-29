package com.example.price_fetcher.controller;

import com.example.price_fetcher.service.PriceProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/prices")
public class PriceController {

    private final PriceProducer priceProducer;

    public PriceController(PriceProducer priceProducer) {
        this.priceProducer = priceProducer;
    }

    @PostMapping("/update")
    public ResponseEntity<String> sendPriceUpdate(@RequestBody String priceUpdate) {
        priceProducer.sendPriceUpdate(priceUpdate);
        return ResponseEntity.ok("Price update sent: " + priceUpdate);
    }
}
