package com.Realty.RealtyWeb.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TransactionType {
    월세("월세"),
    전세("전세"),
    매매("매매"),
    단기("단기"),
    기타("기타");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TransactionType fromValue(String value) {
        for (TransactionType transactionType : TransactionType.values()) {
            if (transactionType.getValue().equalsIgnoreCase(value)) {
                return transactionType;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}

