package com.Realty.RealtyWeb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class CodefRequestDTO {
    @JsonProperty("organization")
    private String organization;           // 기관코드 (ex: "0002")
    @JsonProperty("phoneNo")
    private String phoneNo;                // 전화번호
    @JsonProperty("password")
    private String password;               // 평문 비밀번호 (RSA 암호화 전)

    @JsonProperty("inquiryType")
    private String inquiryType;            // 조회구분 (0~3)
    @JsonProperty("uniqueNo")
    private String uniqueNo;               // 부동산 고유번호
    @JsonProperty("realtyType")
    private String realtyType;             // 부동산 구분

    @JsonProperty("addr_sido")
    private String addr_sido;              // 주소_시도
    @JsonProperty("address")
    private String address;                // 간편검색 주소

    @JsonProperty("recordStatus")
    private String recordStatus;           // 등기기록상태

    @JsonProperty("addr_dong")
    private String addr_dong;              // 주소_동
    @JsonProperty("addr_lotNumber")
    private String addr_lotNumber;         // 주소_지번
    @JsonProperty("inputSelect")
    private String inputSelect;            // 입력선택
    @JsonProperty("buildingName")
    private String buildingName;           // 건물명칭
    @JsonProperty("dong")
    private String dong;                   // 동 (집합건물)
    @JsonProperty("ho")
    private String ho;                     // 호 (집합건물)

    @JsonProperty("addr_sigungu")
    private String addr_sigungu;           // 주소_시군구
    @JsonProperty("addr_roadName")
    private String addr_roadName;          // 주소_도로명
    @JsonProperty("addr_buildingNumber")
    private String addr_buildingNumber;    // 주소_건물번호

    @JsonProperty("jointMortgageJeonseYN")
    private String jointMortgageJeonseYN;  // 공동담보/전세목록 포함여부
    @JsonProperty("tradingYN")
    private String tradingYN;              // 매매목록 포함여부
    @JsonProperty("listNumber")
    private String listNumber;             // 목록번호
    @JsonProperty("electronicClosedYN")
    private String electronicClosedYN;     // 전산폐쇄 포함여부

    @JsonProperty("ePrepayNo")
    private String ePrepayNo;              // 선불전자지급수단 번호

    @JsonProperty("ePrepayPass")
    private String ePrepayPass;            // 선불전자지급수단 비밀번호

    @JsonProperty("issueType")
    private String issueType;              // 발행구분
    @JsonProperty("startPageNo")
    private String startPageNo;            // 시작 페이지 번호
    @JsonProperty("pageCount")
    private String pageCount;              // 조회 페이지 수
    @JsonProperty("originData")
    private String originData;             // 원문 데이터
    @JsonProperty("originDataYN")
    private String originDataYN;           // 원문 포함 여부
    @JsonProperty("warningSkipYN")
    private String warningSkipYN;          // 경고 무시 여부
    @JsonProperty("registerSummaryYN")
    private String registerSummaryYN;      // 등기사항요약 출력 여부

    @JsonProperty("applicationType")
    private String applicationType;        // 신청구분

    @JsonProperty("selectAddress")
    private String selectAddress;          // 주소 리스트 선택 여부

    @JsonProperty("isIdentityViewYn")
    private String isIdentityViewYn;       // 주민등록번호 공개여부

    @JsonProperty("identityList")
    private List<Identity> identityList;   // 주민등록번호 리스트

    @Data
    public static class Identity {
        @JsonProperty("reqIdentity")
        private String reqIdentity;        // 주민등록번호 (13자리)
    }
}
