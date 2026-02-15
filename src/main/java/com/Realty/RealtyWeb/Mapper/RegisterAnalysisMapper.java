//package com.Realty.RealtyWeb.Mapper;
//
//import com.Realty.RealtyWeb.dto.CodefResponseDTO;
//import com.Realty.RealtyWeb.dto.RegisterAnalysisDTO;
//import java.time.LocalDateTime;
//
//import java.time.format.DateTimeFormatter;
//import java.util.stream.Collectors;
//
//public class RegisterAnalysisMapper {
//    // Codef 답변과 매핑
//
//    public static RegisterAnalysisDTO fromCodefResponse(CodefResponseDTO dto, Long pid, String userid) {
//        CodefResponseDTO.PropertyBasicInfo info = dto.getPropertyInfo();
//        CodefResponseDTO.RiskReport report = dto.getRiskReport();
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime issueDate = LocalDateTime.parse(info.getResPublishDate(), formatter);
//
//        // 위험 키워드 추출
//        String keywords = report.getRisks().stream()
//                .map(CodefResponseDTO.RiskDetail::getCategory)
//                .distinct()
//                .collect(Collectors.joining(", "));
//
//        String warnings = report.getRisks().stream()
//                .map(CodefResponseDTO.RiskDetail::getDescription)
//                .distinct()
//                .collect(Collectors.joining(" / "));
//
//        return RegisterAnalysisDTO.builder()
//                .pid(pid)
//                .userid(userid)
//                .owner(info.getResUserNm())
//                .issueDate(issueDate)
//                .riskLevel(report.getOverallRisk())
//                .riskKeywords(keywords)
//                .mainWarnings(warnings)
//                .maxClaim(null)
//                .protectedAmount(null)
//                .pdfBase64(dto.getResOriGinalData())
//                .build();
//    }
//
//
//}
