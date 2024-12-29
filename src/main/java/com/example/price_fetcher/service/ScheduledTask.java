package com.example.price_fetcher.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {
    private final PriceFetcherService priceFetcherService;

    public ScheduledTask(PriceFetcherService priceFetcherService) {
        this.priceFetcherService = priceFetcherService;
    }

    @Scheduled(fixedRate = 120000) // Run every 2 minutes
    public void fetchPricesTask() {
        priceFetcherService.fetchAndPublishPrices();
    }
}
