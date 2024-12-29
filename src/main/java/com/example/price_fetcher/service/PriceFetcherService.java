package com.example.price_fetcher.service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Service
public class PriceFetcherService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final WebClient webClient;
    private static final String TOPIC = "price-updates";
    private static final String API_URL = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin,ethereum&vs_currencies=usd";

    public PriceFetcherService(KafkaTemplate<String, String> kafkaTemplate, WebClient.Builder webClientBuilder) {
        this.kafkaTemplate = kafkaTemplate;
        this.webClient = webClientBuilder.baseUrl(API_URL).build();
    }

    public void fetchAndPublishPrices() {
        webClient.get()
                .retrieve()
                .bodyToMono(Map.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5)) // Retry up to 3 times with a 5-second delay
                        .doAfterRetry(retrySignal ->
                                System.err.println("Retrying due to: " + retrySignal.failure().getMessage())))
                .doOnError(this::handleError)
                .subscribe(prices -> processPrices((Map<String, Map<String, Object>>) prices));
    }

    private void processPrices(Map<String, Map<String, Object>> prices) { // Changed value type to Object
        if (prices != null) {
            prices.forEach((crypto, data) -> {
                Object priceObject = data.get("usd");
                if (priceObject instanceof Number) { // Check if the object is a number
                    double price = ((Number) priceObject).doubleValue(); // Safely convert to double
                    String message = crypto + ": " + price + " USD";
                    kafkaTemplate.send(TOPIC, message);
                    System.out.println("Published: " + message);
                } else {
                    System.err.println("Skipped publishing for " + crypto + ": Price is null or invalid");
                }
            });
        } else {
            System.err.println("API response is null or invalid.");
        }
    }


    private void handleError(Throwable error) {
        if (error instanceof WebClientResponseException) {
            System.err.println("HTTP error: " + ((WebClientResponseException) error).getStatusCode());
        } else {
            System.err.println("Unexpected error: " + error.getMessage());
        }
    }
}