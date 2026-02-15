package com.Realty.RealtyWeb.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum LoanAvailability {
    가능("가능"),
    불가능("불가능");

    private final String value;

    LoanAvailability(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LoanAvailability fromValue(String value) {
        for (LoanAvailability direction : LoanAvailability.values()) {
            if (direction.getValue().equalsIgnoreCase(value)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
