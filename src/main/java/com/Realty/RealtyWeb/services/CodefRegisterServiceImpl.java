package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.dto.*;
import com.Realty.RealtyWeb.enums.Purpose;
import com.Realty.RealtyWeb.enums.TransactionType;
import com.Realty.RealtyWeb.services.analyzer.RegisterAnalyzer;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import io.codef.api.EasyCodefUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodefRegisterServiceImpl implements CodefRegisterService {

    private final RegisterAnalysisService registerAnalysisService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EasyCodef codef;


    // private final HashMap<String, HashMap<String, Object>> twoWayCache = new HashMap<>();
    // twoWayCache 사실 필요없음

    // 이건 2차 인증 걸러내기 용
    @Override
    public JsonNode requestRegisterFirst(CodefRequestDTO dto, String address) throws JsonProcessingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException, InterruptedException {

        String encPw = EasyCodefUtil.encryptRSA(dto.getPassword(), codef.getPublicKey());

        HashMap<String, Object> params = buildBaseParam(dto, encPw, address);

        String raw = codef.requestProduct(
                "/v1/kr/public/ck/real-estate-register/status",
                EasyCodefServiceType.DEMO,
                params
        );

        JsonNode node = objectMapper.readTree(raw);

        // 디버깅 로그 추가
        System.out.println("▶ PARAM 디버깅");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            System.out.println("  ↳ " + entry.getKey() + " = " + entry.getValue());
        }


//        /* 추가 인증 필요 - 이것도 필요없긴 함 */
//        if (isTwoWayRequired(node)) {
//            // jti 를 캐시-키로 사용
//            String jti = node.path("data").path("jti").asText();
//            twoWayCache.put(jti, params);   // ← 1차 파라미터 저장
//        }
        return node;                   // JSON 혹은 binary wrapper

    }

    // 얘가 메인
    @Override
    public JsonNode requestByUnique(UniqueNoRequestDTO dto) {
        try {
            String encPw = EasyCodefUtil.encryptRSA(dto.getPassword(), codef.getPublicKey());

            HashMap<String, Object> p = new HashMap<>();
            p.put("organization", "0002");
            p.put("phoneNo", dto.getPhoneNo());
            p.put("password", encPw);

            /* 핵심 차이점  */
            p.put("uniqueNo", dto.getUniqueNo()); // 사용자가 고른 값

            /* 선택 옵션 */
            if (dto.getEPrepayNo() != null) p.put("ePrepayNo", dto.getEPrepayNo());
            if (dto.getEPrepayPass() != null) p.put("ePrepayPass", dto.getEPrepayPass());
            p.put("recordStatus", "0");
            p.put("inquiryType", dto.getInquiryType());
            p.put("issueType", dto.getIssueType());
            p.put("jointMortgageJeonseYN", dto.getJointMortgageJeonseYN());
            p.put("tradingYN", dto.getTradingYN());
            p.put("electronicClosedYN", dto.getElectronicClosedYN());
            p.put("selectAddress", dto.getSelectAddress());
            p.put("isIdentityViewYN", dto.getIsIdentityViewYN());
            p.put("originDataYN", dto.getOriginDataYN());
            p.put("warningSkipYN", dto.getWarningSkipYN());

            // ✅ 디버깅 로그 추가
            System.out.println("▶ PARAM 디버깅");
            for (Map.Entry<String, Object> entry : p.entrySet()) {
                System.out.println("  ↳ " + entry.getKey() + " = " + entry.getValue());
            }

            String raw = codef.requestProduct(
                    "/v1/kr/public/ck/real-estate-register/status",
                    EasyCodefServiceType.DEMO,
                    p);

            raw = raw == null ? "" : raw.trim();

            // ▸ percent-encoded JSON (예: "%7B%22result%22...")
            if (raw.startsWith("%")) {
                String decoded = URLDecoder.decode(raw, StandardCharsets.UTF_8);
                return objectMapper.readTree(decoded);
            }

            // ▸ 정상 JSON
            if (raw.startsWith("{") || raw.startsWith("[")) {
                return objectMapper.readTree(raw);
            }

            // ▸ PDF(Base64) 등 바이너리
            ObjectNode wrapper = objectMapper.createObjectNode();
            wrapper.put("binaryData", raw);
            return wrapper;

        } catch (Exception e) {
            throw new RuntimeException("고유번호 조회 실패", e);
        }
    }

    @Override
    public boolean isTwoWayRequired(JsonNode response) {
        JsonNode data = response.path("data");
        return data.has("continue2Way") && data.path("continue2Way").asBoolean();
    }


    private HashMap<String, Object> buildBaseParam(CodefRequestDTO dto, String encryptedPw, String address) {
        HashMap<String, Object> p = new HashMap<>();
        p.put("organization", "0002");
        p.put("phoneNo", dto.getPhoneNo());
        p.put("password", encryptedPw);
        p.put("inquiryType", dto.getInquiryType());
        p.put("issueType", dto.getIssueType());

        // inquiryType == "1" 최소 필드
        p.put("address", address);
        p.put("recordStatus", dto.getRecordStatus());
        p.put("jointMortgageJeonseYN", dto.getJointMortgageJeonseYN());
        p.put("tradingYN", dto.getTradingYN());
        p.put("electronicClosedYN", dto.getElectronicClosedYN());
        p.put("selectAddress", "0");
        p.put("ePrepayNo", dto.getEPrepayNo());
        p.put("ePrepayPass", dto.getEPrepayPass());
        p.put("originDataYN", dto.getOriginDataYN());
        p.put("warningSkipYN", dto.getWarningSkipYN());
        return p;
    }

//    @Override
//    public Long parseFinalResult(JsonNode root, Long pid, String userId, HouseInfoDTO houseInfo) {
//
//        System.out.println("▶ 분석 응답 수신 (전체 JSON):\n" + root.toPrettyString());
//
//        try {
//            JsonNode dataNode = root.path("data");  // ✅ 핵심 변경
//// 로그 추가
//            System.out.println("▶ dataNode 구조 확인:");
//            System.out.println(dataNode.toPrettyString());
//
//            System.out.println("▶ dataNode 필드 목록:");
//            dataNode.fieldNames().forEachRemaining(f -> System.out.println(" - " + f));
//
//            // ───── 1. 기본 부동산 정보 추출 ─────
//            System.out.println("▶ 1단계: 기본 필드 추출 시작");
//
//            JsonNode entries = dataNode.path("resRegisterEntriesList");
//            if (entries == null || !entries.isArray() || entries.size() == 0) {
//                throw new IllegalArgumentException("resRegisterEntriesList가 존재하지 않음");
//            }
//
//            JsonNode entry = entries.get(0);
//            String address = entry.path("resRealty").asText();
//            String uniqueNo = entry.path("commUniqueNo").asText();
//            String issueRaw = entry.path("resPublishDate").asText(); // 예: "20250521"
//            String regOffice = entry.path("commCompetentRegistryOffice").asText();
//
//            System.out.println("  ↳ 주소: " + address);
//            System.out.println("  ↳ 고유번호: " + uniqueNo);
//            System.out.println("  ↳ 발급일자(raw): " + issueRaw);
//            System.out.println("  ↳ 등기소: " + regOffice);
//
//// ───── 2. 날짜 파싱 ─────
//            System.out.println("▶ 2단계: 발급일자 파싱");
//            LocalDateTime issueDate;
//
//            if (issueRaw.matches("\\d{8}")) {
//                // "yyyyMMdd" 형식
//                issueDate = LocalDate.parse(issueRaw, DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay();
//            } else if (issueRaw.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
//                // "yyyy-MM-dd HH:mm:ss" 형식
//                issueDate = LocalDateTime.parse(issueRaw, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            } else {
//                throw new IllegalArgumentException("지원하지 않는 발급일자 형식: " + issueRaw);
//            }
//
//            System.out.println("  ↳ 발급일자 파싱 완료: " + issueDate);
//
//            // ───── 3. 등기 내역 분석 ─────
//            System.out.println("▶ 3단계: 등기 내역 분석 시작");
//            JsonNode hisList = entry.path("resRegistrationHisList");
//            String owner = extractOwnerFromLatestTransfer(hisList);
//            if (hisList == null || !hisList.isArray()) {
//                throw new IllegalArgumentException("resRegistrationHisList가 존재하지 않음");
//            }
//            System.out.println("  ↳ 등록 내역 수: " + hisList.size());
//
//            RegisterAnalyzer.Result r = RegisterAnalyzer.analyze(hisList, address);
//            System.out.println("  ↳ 분석 완료 - 위험도: " + r.overallRisk);
//            System.out.println("  ↳ 최대 채권액: " + r.maxClaim);
//            System.out.println("  ↳ 보호 금액: " + r.protectedAmount);
//
//            // ───── 4. DB 저장용 DTO 생성 및 저장 ─────
//            System.out.println("▶ 4단계: RegisterAnalysisDTO 생성 및 저장");
//            RegisterAnalysisDTO analysisDto = RegisterAnalysisDTO.builder()
//                    .pid(pid)
//                    .owner(owner)
//                    .userid(userId)
//                    .issueDate(issueDate)
//                    .riskLevel(r.overallRisk)
//                    .riskKeywords(
//                            r.riskDetails.stream().map(RiskDetail::getKeyword)
//                                    .collect(Collectors.joining(", ")))
//                    .mainWarnings(
//                            r.riskDetails.stream()
//                                    .map(d -> d.getTitle() + " - " + d.getDescription())  // ✅ 제목 + 설명 결합
//                                    .collect(Collectors.joining(" / "))
//                    )
//                    .maxClaim(r.maxClaim)
//                    .protectedAmount(r.protectedAmount)
//                    .purpose(Purpose.valueOf(houseInfo.getPurpose()))
//                    .transactionType(TransactionType.valueOf(houseInfo.getTransactionType()))
//                    .price(houseInfo.getPrice())
//                    .rentPrc(houseInfo.getRentPrc())
//                    .exclusiveArea(houseInfo.getExclusiveArea())
//                    .pdfBase64(dataNode.path("resOriGinalData").asText())  // ✅ 수정
//                    .riskDetails(r.riskDetails)
//                    .build();
//
//
//            System.out.println("  ↳ DB 저장 완료");
//
//            // ───── 5. 클라이언트 응답 DTO 구성 ─────
//            System.out.println("▶ 5단계: 응답 DTO 구성 시작");
//
//            CodefResponseDTO.PropertyBasicInfo info = CodefResponseDTO.PropertyBasicInfo.builder()
//                    .resRealty(address)
//                    .resUserNm(owner)
//                    .commUniqueNo(uniqueNo)
//                    .resPublishDate(issueRaw)
//                    .commCompetentRegistryOffice(regOffice)
//                    .build();
//
//            List<CodefResponseDTO.RiskDetail> detailDtos = r.riskDetails.stream()
//                    .map(d -> CodefResponseDTO.RiskDetail.builder()
//                            .category(d.getTitle())
//                            .level(d.getLevel())
//                            .description(d.getDescription())
//                            .build())
//                    .toList();
//
//            CodefResponseDTO.RiskReport report = CodefResponseDTO.RiskReport.builder()
//                    .overallRisk(r.overallRisk)
//                    .risks(detailDtos)
//                    .build();
//
//            CodefResponseDTO finalDto = CodefResponseDTO.builder()
//                    .propertyInfo(info)
//                    .resOriGinalData(dataNode.path("resOriGinalData").asText())  // ✅ 수정
//                    .riskReport(report)
//                    .build();
//
//            System.out.println("▶ 최종 응답 구성 완료");
//            //return finalDto;
//            return registerAnalysisService.save(analysisDto);
//
//        } catch (Exception e) {
//            System.err.println("▶ 분석 중 예외 발생: " + e.getMessage());
//            e.printStackTrace();
//            throw new RuntimeException("등기부 분석 중 오류 발생", e);
//        }
//    }

    private String extractOwnerFromLatestTransfer(JsonNode hisList) {
        String fallback = "미상";
        String lastMatchedContent = null;

        for (int i = hisList.size() - 1; i >= 0; i--) {
            JsonNode group = hisList.get(i);
            if (!"갑구".equals(group.path("resType").asText())) continue;

            JsonNode contentsList = group.path("resContentsList");

            for (int j = contentsList.size() - 1; j >= 0; j--) {
                JsonNode item = contentsList.get(j);
                JsonNode detailList = item.path("resDetailList");

                if (detailList.size() < 5) continue; // 필수 인덱스 존재 여부 확인

                String purpose = detailList.get(1).path("resContents").asText().trim(); // 등기목적
                if (!"소유권이전".equals(purpose)) continue;

                // ✅ 이 항목이 우리가 원하는 마지막 "소유권이전" 항목
                String content = detailList.get(4).path("resContents").asText();
                lastMatchedContent = content;

                // 바로 추출 시도
                Matcher m = Pattern.compile("소유자\\s+([가-힣]{2,10})").matcher(content);
                if (m.find()) {
                    String owner = m.group(1);
                    System.out.println("  ✅ 소유자 추출 성공: " + owner);
                    return owner;
                } else {
                    System.out.println("  ⚠ 소유자 정규식 매칭 실패: " + content);
                }
            }
        }

        System.out.println("⚠ 소유권이전 항목은 있었지만 소유자 추출 실패 → fallback 사용");
        if (lastMatchedContent != null) System.out.println("🔎 마지막 내용:\n" + lastMatchedContent);
        return fallback;
    }




}
/*
    @Override
    public JsonNode requestRegisterFirst(CodefRequestDTO dto) throws JsonProcessingException {
        HashMap<String, Object> params = new HashMap<>();

        try {
            String rawPw = dto.getPassword();
            String encryptedPw = EasyCodefUtil.encryptRSA(rawPw, codef.getPublicKey());

            params.put("organization", "0002");
            params.put("phoneNo", dto.getPhoneNo());
            params.put("password", encryptedPw);
            params.put("inquiryType", dto.getInquiryType());
            params.put("issueType", dto.getIssueType());

            // 타입 분기
            switch (dto.getInquiryType()) {
                case "0" -> {
                    params.put("uniqueNo", dto.getUniqueNo());
                    params.put("ePrepayNo", dto.getEPrepayNo());
                    params.put("ePrepayPass", dto.getEPrepayPass());
                }
                case "1" -> {
                    if (dto.getRealtyType() != null) params.put("realtyType", dto.getRealtyType());
                    if (dto.getAddr_sido() != null) params.put("addr_sido", dto.getAddr_sido());
                    if (dto.getAddress() != null) params.put("address", dto.getAddress());
                    if (dto.getRecordStatus() != null) params.put("recordStatus", dto.getRecordStatus());
                    if (dto.getStartPageNo() != null) params.put("startPageNo", dto.getStartPageNo());
                    if (dto.getPageCount() != null) params.put("pageCount", dto.getPageCount());
                    params.put("ePrepayNo", dto.getEPrepayNo());
                    params.put("ePrepayPass", dto.getEPrepayPass());
                }
                case "2" -> {
                    params.put("realtyType", dto.getRealtyType());
                    params.put("addr_sido", dto.getAddr_sido());
                    params.put("addr_dong", dto.getAddr_dong());
                    params.put("addr_lotNumber", dto.getAddr_lotNumber());
                    if ("1".equals(dto.getRealtyType())) params.put("inputSelect", dto.getInputSelect());
                    if (dto.getBuildingName() != null) params.put("buildingName", dto.getBuildingName());
                }
                case "3" -> {
                    params.put("addr_sigungu", dto.getAddr_sigungu());
                    params.put("addr_roadName", dto.getAddr_roadName());
                    params.put("addr_buildingNumber", dto.getAddr_buildingNumber());
                }
                default -> throw new IllegalArgumentException("inquiryType이 올바르지 않음: " + dto.getInquiryType());
            }

            if ("1".equals(dto.getRealtyType())) {
                params.put("dong", dto.getDong());
                params.put("ho", dto.getHo());
                params.put("applicationType", dto.getApplicationType());
            }
            if (dto.getJointMortgageJeonseYN() != null) params.put("jointMortgageJeonseYN", dto.getJointMortgageJeonseYN());
            if (dto.getTradingYN() != null) params.put("tradingYN", dto.getTradingYN());
            if (dto.getListNumber() != null) params.put("listNumber", dto.getListNumber());
            if (dto.getElectronicClosedYN() != null) params.put("electronicClosedYN", dto.getElectronicClosedYN());
            if ("3".equals(dto.getIssueType()) && dto.getOriginData() != null)
                params.put("originData", dto.getOriginData().replace("\n", "\\n"));
            if (dto.getOriginDataYN() != null) params.put("originDataYN", dto.getOriginDataYN());
            if (dto.getWarningSkipYN() != null) params.put("warningSkipYN", dto.getWarningSkipYN());
            if (dto.getRegisterSummaryYN() != null) params.put("registerSummaryYN", dto.getRegisterSummaryYN());
            if (dto.getSelectAddress() != null) params.put("selectAddress", dto.getSelectAddress());
            if (dto.getIsIdentityViewYn() != null) params.put("isIdentityViewYn", dto.getIsIdentityViewYn());
            if ("1".equals(dto.getIsIdentityViewYn()) && dto.getIdentityList() != null)
                params.put("identityList", dto.getIdentityList());


            // params.entrySet().removeIf(e -> e.getValue() == null);

            // ✅ 디버깅 로그 추가
            System.out.println("▶ PARAM 디버깅");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                System.out.println("  ↳ " + entry.getKey() + " = " + entry.getValue());
            }

            // EasyCodef 요청
            String productUrl = "/v1/kr/public/ck/real-estate-register/status";
            String resultJson = codef.requestProduct(productUrl, EasyCodefServiceType.DEMO, params);

            return objectMapper.readTree(resultJson);

        } catch (Exception e) {
            System.err.println("▶ 예외 발생: " + e.getMessage());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                System.err.println("  ↳ " + entry.getKey() + " = " + entry.getValue());
            }
            throw new RuntimeException("등기부등본 요청 실패", e);
        }
    }



    @Override
    public CodefResponseDTO parseFinalResult(JsonNode response, Long pid, String userid) {
        ObjectMapper mapper = new ObjectMapper();

        // 1. 기본 부동산 정보
        JsonNode propertyNode = response.path("propertyBasicInfo");
        String owner = propertyNode.path("resUserNm").asText();                    // 소유자명
        String propertyName = propertyNode.path("resRealty").asText();            // 부동산명 (주소)
        String uniqueNo = propertyNode.path("commUniqueNo").asText();             // 고유번호
        String publishDate = propertyNode.path("resPublishDate").asText();        // 발급일자
        String registryOffice = propertyNode.path("commCompetentRegistryOffice").asText(); // 등기소명

        // 2. 발급일자 파싱
        LocalDateTime issueDate = LocalDateTime.parse(publishDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 3. 등기 내역 분석
        JsonNode regList = response.path("resRegistrationHisList");
        RegisterAnalyzer.Result analysis = RegisterAnalyzer.analyze(regList, propertyName);
        RegisterAnalysisDTO dto = RegisterAnalysisDTO.builder()
                .pid(pid)
                .userid(userid)
                .owner(owner)
                .issueDate(issueDate)
                .riskLevel(analysis.overallRisk)
                .riskKeywords(analysis.riskDetails.stream().map(RiskDetail::getTitle).collect(Collectors.joining(", ")))
                .mainWarnings(analysis.riskDetails.stream().map(RiskDetail::getDescription).collect(Collectors.joining(" / ")))
                .maxClaim(analysis.maxClaim)
                .protectedAmount(analysis.protectedAmount)
                .pdfBase64(response.path("resOriGinalData").asText())
                .riskDetails(analysis.riskDetails)
                .build();

        registerAnalysisService.save(dto);

        // 4. 기본 정보 DTO 구성
        CodefResponseDTO.PropertyBasicInfo info = CodefResponseDTO.PropertyBasicInfo.builder()
                .resUserNm(owner)
                .resRealty(propertyName)
                .commUniqueNo(uniqueNo)
                .resPublishDate(publishDate)
                .commCompetentRegistryOffice(registryOffice)
                .build();

        // 5. 리스크 상세 리스트 구성
        List<CodefResponseDTO.RiskDetail> risks = analysis.riskDetails.stream()
                .map(d -> CodefResponseDTO.RiskDetail.builder()
                        .category(d.getTitle())
                        .level(d.getLevel())
                        .description(d.getDescription())
                        .build())
                .toList();

        CodefResponseDTO.RiskReport report = CodefResponseDTO.RiskReport.builder()
                .overallRisk(analysis.overallRisk)
                .risks(risks)
                .build();

        // 6. 최종 응답 DTO 생성
        return CodefResponseDTO.builder()
                .propertyInfo(info)
                .resOriGinalData(response.path("resOriGinalData").asText())
                .riskReport(report)
                .build();
    }



*/



