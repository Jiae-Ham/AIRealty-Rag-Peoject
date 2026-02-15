package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.dto.ClovaRequestDTO;
import com.Realty.RealtyWeb.dto.ClovaResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClovaServiceImpl implements ClovaService {

   private final WebClient webClient;
   @Value("${clova.api.task-id}")
   private String taskId;

   @Override
   public Mono<ClovaResponseDTO> getChatCompletion(ClovaRequestDTO request) {
      try {
         ObjectMapper mapper = new ObjectMapper();
         String json = mapper.writeValueAsString(request);
         log.info("\n🛠 클로바 요청 JSON:\n{}", json);
      } catch (Exception e) {
         log.error("❌ JSON 변환 오류", e);
      }

      return webClient.post()
              .uri("/testapp/v2/tasks/{taskId}/chat-completions", taskId)
              .bodyValue(request)
              .retrieve()
              .bodyToMono(ClovaResponseDTO.class);
   }
}
