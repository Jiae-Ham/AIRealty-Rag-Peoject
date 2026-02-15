package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.dto.CodefResponseDTO;
import com.Realty.RealtyWeb.dto.RegisterAnalysisDTO;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface RegisterAnalysisService {
    Long save(RegisterAnalysisDTO dto);

    RegisterAnalysisDTO getByIdAndUser(Long id, String userid);
    List<RegisterAnalysisDTO> getAllByUser(String userid);

    void deleteByIdAndUser(Long id, String userId);

}
