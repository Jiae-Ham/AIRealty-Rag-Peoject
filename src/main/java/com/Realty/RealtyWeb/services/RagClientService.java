package com.Realty.RealtyWeb.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.nio.file.Files;

@Service
@RequiredArgsConstructor
public class RagClientService {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8000") // 또는 docker-compose 내 서비스명
            .build();

    public String analyzeWithRag(JsonNode uniqueResponse) {
        try {
            // ① JSON → 임시 파일 저장
            File tempFile = File.createTempFile("register_", ".json");
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(tempFile, uniqueResponse);

            // ② multipart/form-data 요청
            String ragAnswer = webClient.post()
                    .uri("/analyze")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("file", new FileSystemResource(tempFile)))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(json -> json.path("result").path("content").asText())
                    .block();

            // ③ 파일 삭제 (선택적)
            tempFile.delete(); // 또는 deleteOnExit()

            return ragAnswer;

        } catch (Exception e) {
            throw new RuntimeException("RAG 분석 중 오류 발생", e);
        }
    }
}
