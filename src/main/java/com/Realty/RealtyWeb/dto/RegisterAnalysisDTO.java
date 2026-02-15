package com.Realty.RealtyWeb.dto;

import com.Realty.RealtyWeb.Entity.RegisterAnalysisEntity;
import com.Realty.RealtyWeb.enums.Purpose;
import com.Realty.RealtyWeb.enums.RiskLevel;
import com.Realty.RealtyWeb.enums.TransactionType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Setter
@Getter
public class RegisterAnalysisDTO {
    private Long id;
    private Long pid;
    private String userid;

    /*
    private String owner;
    private LocalDateTime issueDate;

    private String riskLevel;
    private String riskKeywords;
    private String mainWarnings;

    private BigDecimal maxClaim;
    private BigDecimal protectedAmount;
    */
    private String ragAnswer;  // LLM 분석 결과 전체 문장 저장

    private Purpose purpose;
    private TransactionType transactionType;
    private BigDecimal price;
    private BigDecimal rentPrc;
    private BigDecimal exclusiveArea;

    private String pdfBase64;

    private List<RiskDetail> riskDetails;

    // DTO -> 엔티티

    public static RegisterAnalysisEntity toEntity(RegisterAnalysisDTO dto) {
        return RegisterAnalysisEntity.builder()
                .id(dto.getId())
                .userid(dto.getUserid())
                .pid(dto.getPid())
                .ragAnswer(dto.getRagAnswer())
                .purpose(Purpose.valueOf(dto.getPurpose().toString()))
                .transactionType(TransactionType.valueOf(dto.getTransactionType().toString()))
                .price(dto.getPrice())
                .rentPrc(dto.getRentPrc())
                .exclusiveArea(dto.getExclusiveArea())
                .pdf_base64(dto.getPdfBase64())
                .build();
    }

    // 엔티티 -> DTO
    public static RegisterAnalysisDTO fromEntity(RegisterAnalysisEntity entity) {
        return RegisterAnalysisDTO.builder()
                .id(entity.getId())
                .userid(entity.getUserid())
                .pid(entity.getPid())
                .ragAnswer(entity.getRagAnswer())
                .purpose(Purpose.valueOf(entity.getPurpose().toString()))
                .transactionType(TransactionType.valueOf(entity.getTransactionType().toString()))
                .price(entity.getPrice())
                .rentPrc(entity.getRentPrc())
                .exclusiveArea(entity.getExclusiveArea())
                .pdfBase64(entity.getPdf_base64())
                .build();
    }
}
