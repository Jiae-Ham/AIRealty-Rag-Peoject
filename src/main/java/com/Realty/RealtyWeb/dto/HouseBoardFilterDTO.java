package com.Realty.RealtyWeb.dto;

import com.Realty.RealtyWeb.enums.Purpose;
import com.Realty.RealtyWeb.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseBoardFilterDTO {

    private Purpose purpose;             // 매물 종류 (원룸, 빌라, 오피스텔, 아파트, 상가, 기타)
    private TransactionType transactionType;  // 거래 방식 (월세, 전세, 매매)
    private Integer minPrice;            // 최소 가격
    private Integer maxPrice;            // 최대 가격
    private Integer minExclusiveArea;    // 최소 전용 면적 (m²)
    private Integer maxExclusiveArea;    // 최대 전용 면적 (m²)
    private Integer minRentPrc; // 월세
    private Integer maxRentPrc; // 월세
    private Integer minParkingPerHouseholdCount; // 주차 대수
    private String addrCode; //지역코드
/*
    private Integer minFloor;            // 최소 층수
    private Integer maxFloor;            // 최대 층수
    private Integer builtYear;           // 사용 승인일
    private Boolean petAllowed;          // 반려동물 가능 여부
    private Boolean parkingAvailable;    // 주차 가능 여부
*/
}
