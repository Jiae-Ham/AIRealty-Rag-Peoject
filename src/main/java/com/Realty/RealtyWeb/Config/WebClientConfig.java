package com.Realty.RealtyWeb.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebClientConfig {
    @Value("${clova.api.url}")
    private String baseUrl;
    @Value("${clova.api.request-id}")
    private String requestId;
    @Value("${clova.api.api-key}")
    private String apiKey;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer "+ apiKey)
                .defaultHeader("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
