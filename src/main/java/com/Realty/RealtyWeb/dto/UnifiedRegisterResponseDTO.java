package com.Realty.RealtyWeb.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnifiedRegisterResponseDTO {
    private boolean requiresTwoWay;
    private CodefTwoWayAuthResponseDTO twoWayInfo; // 2차 인증 필요 시
    private CodefResponseDTO finalResult;          // 2차 없이 바로 응답 가능 시
}
