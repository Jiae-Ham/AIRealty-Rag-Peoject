package com.Realty.RealtyWeb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodefTwoWayAuthResponseDTO {
    private boolean continue2Way; //추가 인증 필요여부
    private String method; //인증 방식
    private int jobIndex;
    private int threadIndex;
    private String jti; //트랜잭션 ID
    private long twoWayTimestamp; //인증 시간

    private ExtraInfo extraInfo;

    @Data
    @Builder

    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExtraInfo {
        private List<AddressCandidate> resAddrList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressCandidate {
        private String commUniqueNo; //부동산 고유번호
        private String commAddrLotNumber; //부동산 소재지번
        private String resUserNm; //소유자명
        private String resState; //상태
        private String resType; //구분
    }

}
