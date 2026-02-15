package com.Realty.RealtyWeb.enums;

import java.math.BigDecimal;

public enum RegionGuaranteeRule {

    서울("서울특별시", new BigDecimal("50000000"), new BigDecimal("17000000")),
    수도권("수도권 과밀억제권역", new BigDecimal("43000000"), new BigDecimal("15000000")),
    광역시("광역시", new BigDecimal("37000000"), new BigDecimal("13000000")),
    기타("그 외 지역", new BigDecimal("30000000"), new BigDecimal("11000000"));

    private final String regionName;
    private final BigDecimal guaranteeLimit;
    private final BigDecimal maxPriorityRepayment;

    RegionGuaranteeRule(String regionName, BigDecimal guaranteeLimit, BigDecimal maxPriorityRepayment) {
        this.regionName = regionName;
        this.guaranteeLimit = guaranteeLimit;
        this.maxPriorityRepayment = maxPriorityRepayment;
    }

    public BigDecimal getGuaranteeLimit() {
        return guaranteeLimit;
    }

    public BigDecimal getMaxPriorityRepayment() {
        return maxPriorityRepayment;
    }

    public static RegionGuaranteeRule fromAddress(String address) {
        if (address.contains("서울")) return 서울;
        if (address.contains("과천") || address.contains("성남") || address.contains("부천") || address.contains("안양") || address.contains("군포") || address.contains("의왕") || address.contains("하남") || address.contains("광명")) return 수도권;
        if (address.contains("부산") || address.contains("대구") || address.contains("인천") || address.contains("광주") || address.contains("대전") || address.contains("울산")) return 광역시;
        return 기타;
    }
}
