package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.dto.ClovaRequestDTO;
import com.Realty.RealtyWeb.dto.ClovaResponseDTO;
import reactor.core.publisher.Mono;

public interface ClovaService {
    Mono<ClovaResponseDTO> getChatCompletion(ClovaRequestDTO request);
}
