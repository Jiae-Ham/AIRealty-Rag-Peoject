package com.Realty.RealtyWeb.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RiskLevel {
    안전("안전"),
    주의("주의"),
    위험("위험");

    private final String value;

    RiskLevel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RiskLevel fromValue(String value) {
        for (RiskLevel riskLevel : RiskLevel.values()) {
            if (riskLevel.value.equalsIgnoreCase(value)) {
                return riskLevel;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
