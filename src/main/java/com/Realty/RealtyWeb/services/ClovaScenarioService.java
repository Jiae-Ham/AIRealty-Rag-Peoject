package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.dto.ClovaRequestDTO;
import com.Realty.RealtyWeb.dto.ClovaResponseDTO;
import com.Realty.RealtyWeb.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClovaScenarioService {

    private final ClovaService clovaService;

    public Mono<ClovaResponseDTO> runScenario(List<MessageDTO> messages) {
     //요청 DTO 구성
        ClovaRequestDTO request = ClovaRequestDTO.builder()
                .messages(new ArrayList<>(messages)) //복사본
                .topP(0.8)
                .topK(0)
                .temperature(0.5)
                .maxTokens(256)
                .repeatPenalty(5.0)
                .stopBefore(List.of())
                .build();
        // api 호출
        return clovaService.getChatCompletion(request);
    }


}
