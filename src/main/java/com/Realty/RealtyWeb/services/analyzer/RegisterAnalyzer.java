package com.Realty.RealtyWeb.services.analyzer;

import com.Realty.RealtyWeb.dto.RiskDetail;
import com.Realty.RealtyWeb.enums.RegionGuaranteeRule;
import com.Realty.RealtyWeb.enums.RiskKeywordRule;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegisterAnalyzer {

    public static class Result {
        public List<RiskDetail> riskDetails;
        public BigDecimal maxClaim;
        public BigDecimal protectedAmount;
        public String overallRisk;

        public Result(List<RiskDetail> riskDetails, BigDecimal maxClaim, BigDecimal protectedAmount, String overallRisk) {
            this.riskDetails = riskDetails;
            this.maxClaim = maxClaim;
            this.protectedAmount = protectedAmount;
            this.overallRisk = overallRisk;
        }
    }

    public static Result analyze(JsonNode resRegistrationHisList, String fullAddress) {
        List<RiskDetail> result = new ArrayList<>();
        BigDecimal maxClaim = BigDecimal.ZERO;

        System.out.println("▶ 분석 시작: 등록 내역 수 = " + resRegistrationHisList.size());

        for (JsonNode item : resRegistrationHisList) {
            String resType = item.path("resType").asText();
            System.out.println("  ↳ [resType=" + resType + "]");

            if (!resType.equals("갑구") && !resType.equals("을구") && !resType.equals("표제부")) {
                System.out.println("    → 무시됨 (비분석 대상)");
                continue;
            }

            List<RiskDetail> keywordHits = extractRiskKeywords(item);
            System.out.println("    → 위험 키워드 " + keywordHits.size() + "건 발견");

            result.addAll(keywordHits);

            if (resType.equals("을구")) {
                BigDecimal claim = extractMaxClaim(item);
                System.out.println("    → 채권최고액 추출: " + claim);
                if (claim.compareTo(maxClaim) > 0) {
                    maxClaim = claim;
                }
            }
        }

        RegionGuaranteeRule regionRule = RegionGuaranteeRule.fromAddress(fullAddress);
        BigDecimal protectedAmount = regionRule.getGuaranteeLimit();
        String overallRisk = calculateOverallRisk(result);

        System.out.println("▶ 지역 보호한도: " + protectedAmount);
        System.out.println("▶ 분석 총 위험 등급: " + overallRisk);

        return new Result(result, maxClaim, protectedAmount, overallRisk);
    }

    private static BigDecimal extractMaxClaim(JsonNode item) {
        try {
            for (JsonNode entry : item.path("resContentsList")) {
                for (JsonNode detail : entry.path("resDetailList")) {
                    String content = detail.path("resContents").asText();
                    String[] lines = content.split("\\n");
                    for (String line : lines) {
                        if (line.trim().startsWith("&") && line.trim().endsWith("&")) continue;
                        Matcher m = Pattern.compile("채권최고액\\s*금?([\\d,]+)원").matcher(line);
                        if (m.find()) {
                            BigDecimal extracted = new BigDecimal(m.group(1).replace(",", ""));
                            System.out.println("      → 채권최고액 탐지됨: " + extracted);
                            return extracted;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("▶ 채권최고액 추출 실패: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    private static List<RiskDetail> extractRiskKeywords(JsonNode item) {
        Set<RiskKeywordRule> detectedRules = new HashSet<>();

        try {
            for (JsonNode entry : item.path("resContentsList")) {
                for (JsonNode detail : entry.path("resDetailList")) {
                    String content = detail.path("resContents").asText();
                    if (content.contains("말소") || content.contains("취소") || content.contains("해지")) {
                        continue;
                    }
                    String[] lines = content.split("\\n");
                    for (String line : lines) {
                        if (line.trim().startsWith("&") && line.trim().endsWith("&")) continue;

                        Optional<RiskKeywordRule> match = RiskKeywordRule.match(line);
                        match.ifPresent(rule -> {
                            if (detectedRules.add(rule)) {
                                System.out.println("      → 키워드 발견: " + rule.name() + " / " + content);
                            }
                        });
                    }

                }
            }
        } catch (Exception e) {
            System.err.println("▶ 키워드 분석 실패: " + e.getMessage());
        }

        // 중복 제거된 키워드만 RiskDetail로 변환
        return detectedRules.stream()
                .map(rule -> RiskDetail.builder()
                        .level(rule.getLevel())
                        .keyword(rule.name())
                        .title(rule.getTitle())
                        .description(rule.getDescription())
                        .build())
                .collect(Collectors.toList());

    }


    private static String calculateOverallRisk(List<RiskDetail> details) {
        boolean hasHigh = details.stream().anyMatch(d -> "위험".equals(d.getLevel()));
        boolean hasMid = details.stream().anyMatch(d -> "주의".equals(d.getLevel()));
        if (hasHigh) return "위험";
        if (hasMid) return "주의";
        return "안전";
    }
}
