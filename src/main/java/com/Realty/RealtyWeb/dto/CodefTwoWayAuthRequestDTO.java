package com.Realty.RealtyWeb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 2-Way 인증(추가 인증) 요청용 DTO
 *
 * ✔️ 1차 요청에 사용한 모든 필드를 “평면” 구조로 그대로 보냅니다.
 * ✔️ 2-Way 전용 필드(jobIndex · threadIndex · jti · twoWayTimestamp)는
 *    중첩 객체(twoWayInfo)에 담아 전달합니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodefTwoWayAuthRequestDTO {

    /* ───── 2-Way 공통 필드 ───── */
    private String  uniqueNo;   // 사용자가 선택한 부동산 고유번호
    private boolean is2Way;     // true 고정

    /* ───── 1차 요청에서 그대로 가져오는 필드 ───── */
    @JsonProperty("organization")  private String organization;            // "0002"
    @JsonProperty("phoneNo")       private String phoneNo;                 // 휴대폰번호
    private String  password;                                             // 평문 비밀번호
    private String  inquiryType;                                          // "1"
    private String  issueType;                                            // "1"
    private String  address;                                              // 간편검색 주소
    private String  recordStatus;                                         // "0"
    private String  jointMortgageJeonseYN;                                // "0"
    private String  tradingYN;                                            // "0"
    private String  electronicClosedYN;                                   // "0"
    private String  ePrepayNo;                                            // 선불전자지급수단 번호
    private String  ePrepayPass;                                          // 선불전자지급수단 비밀번호
    private String  originDataYN;                                         // "1"
    private String  warningSkipYN;                                        // "0"

    /* ───── 2-Way 전용 정보 ───── */
    private TwoWayInfo twoWayInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TwoWayInfo {
        private int    jobIndex;          // 0
        private int    threadIndex;       // 0
        private String jti;               // 응답에서 받은 jti
        private long   twoWayTimestamp;   // 응답에서 받은 twoWayTimestamp
    }
}
