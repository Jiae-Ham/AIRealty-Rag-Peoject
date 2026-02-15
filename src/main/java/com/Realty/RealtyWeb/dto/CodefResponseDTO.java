package com.Realty.RealtyWeb.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class CodefResponseDTO {

    private PropertyBasicInfo propertyInfo;     // 기본 부동산 정보
    private String resOriGinalData;             // 등기부등본 원문 (Base64)
    private RiskReport riskReport;              // 리스크 분석 결과

    @Data
    @Builder
    public static class PropertyBasicInfo {
        private String resUserNm;                   // 소유자명
        private String resRealty;                   // 부동산명
        private String commUniqueNo;                // 고유번호
        private String resPublishDate;              // 발급일자
        private String commCompetentRegistryOffice; // 관할등기소
    }

    @Data
    @Builder
    public static class RiskReport {
        private String overallRisk;                 // 최종판단 (없음 / 주의 / 위험)
        private List<RiskDetail> risks;             // 리스크 상세 리스트
    }

    @Data
    @Builder
    public static class RiskDetail {
        private String category;                    // 항목명 (예: 근저당권)
        private String level;                       // 상태 (주의 / 위험)
        private String description;                 // 설명
    }
}


