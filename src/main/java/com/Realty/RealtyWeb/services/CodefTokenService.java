package com.Realty.RealtyWeb.services;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class CodefTokenService {

    @Value("${codef.client-id}")
    private String clientId;

    @Value("${codef.client-secret}")
    private String clientSecret;

    private String accessToken;
    private long tokenExpiryTime;

    private static final String TOKEN_URL = "https://oauth.codef.io/oauth/token";

    public synchronized String getAccessToken() {
        long now = System.currentTimeMillis();
        if (accessToken == null || now >= tokenExpiryTime) {
            refreshToken();
        }
        return accessToken;
    }

    private void refreshToken() {
        try {
            String params = "grant_type=client_credentials&scope=read";
            String auth = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TOKEN_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic " + auth)
                    .POST(HttpRequest.BodyPublishers.ofString(params))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> tokenMap = mapper.readValue(response.body(), new TypeReference<>() {});
                this.accessToken = (String) tokenMap.get("access_token");

                int expiresIn = (int) tokenMap.get("expires_in"); // 보통 604800 (일주일)
                this.tokenExpiryTime = System.currentTimeMillis() + (expiresIn - 60) * 1000L; // 1분 여유
            } else {
                throw new RuntimeException("토큰 요청 실패: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Codef 토큰 갱신 실패", e);
        }
    }
}
