package com.Realty.RealtyWeb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniqueNoRequestDTO {

    private Long pid;
    /* ───── 1차 요청에서 그대로 가져오는 필드 ───── */
    @JsonProperty("organization")
    private String organization;            // "0002"
    @JsonProperty("phoneNo")
    private String phoneNo;                 // 휴대폰번호
    private String  password;
    @JsonProperty("uniqueNo") // 평문 비밀번호
    private String uniqueNo;
    @JsonProperty("ePrepayNo")
    private String  ePrepayNo;
    @JsonProperty("ePrepayPass")
    private String  ePrepayPass;
    @JsonProperty("recordStatus")
    private String  recordStatus;
    @Builder.Default private String  inquiryType = "0";                                          // "1"
    @Builder.Default private String  issueType = "1";                                            // "1"// 간편검색 주소
    @Builder.Default private String  jointMortgageJeonseYN = "0";                                // "0"
    @Builder.Default private String  tradingYN = "0";                                            // "0"
    @Builder.Default private String  electronicClosedYN = "0";
    @Builder.Default private String selectAddress = "0";
    @Builder.Default private String isIdentityViewYN = "0";
    @Builder.Default private String  originDataYN = "1";
    @Builder.Default private String  warningSkipYN = "0";
}

