package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.Entity.RegisterAnalysisEntity;
import com.Realty.RealtyWeb.dto.CodefResponseDTO;
import com.Realty.RealtyWeb.dto.CodefResponseDTO.*;
import com.Realty.RealtyWeb.dto.RegisterAnalysisDTO;
import com.Realty.RealtyWeb.repository.RegisterAnalysisRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegisterAnalysisServiceImpl implements RegisterAnalysisService {

    private final RegisterAnalysisRepository registerAnalysisRepository;

    @Override
    public Long save(RegisterAnalysisDTO dto) {
        RegisterAnalysisEntity entity = RegisterAnalysisDTO.toEntity(dto);
        registerAnalysisRepository.save(entity);
        return entity.getId();
    }

    @Override
    public RegisterAnalysisDTO getByIdAndUser(Long id, String userid) {
        RegisterAnalysisEntity entity = registerAnalysisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("분석 결과를 찾을 수 없습니다."));

        if (!entity.getUserid().equals(userid)) {
            throw new AccessDeniedException("다른 사용자의 분석 결과는 조회할 수 없습니다.");

        }

        return RegisterAnalysisDTO.fromEntity(entity);
    }

    @Override
    public List<RegisterAnalysisDTO> getAllByUser(String userid) {
        return registerAnalysisRepository.findAllByUserid(userid).stream()
                .map(RegisterAnalysisDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByIdAndUser(Long id, String userId) {
        RegisterAnalysisEntity entity = registerAnalysisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("분석 결과를 찾을 수 없습니다."));

        if (!entity.getUserid().equals(userId)) {
            throw new AccessDeniedException("다른 사용자의 분석 결과는 삭제할 수 없습니다.");
        }

        registerAnalysisRepository.delete(entity);
    }

}
