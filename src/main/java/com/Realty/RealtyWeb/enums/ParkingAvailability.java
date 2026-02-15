package com.Realty.RealtyWeb.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ParkingAvailability {
    가능("가능"),
    불가능("불가능");

    private final String value;


    ParkingAvailability(String value) {
        this.value = value;
    }
    @JsonValue
    public String getValue() {
        return value;
    }
    @JsonCreator
    public static ParkingAvailability fromValue(String value) {
        for (ParkingAvailability direction : ParkingAvailability.values()) {
            if (direction.getValue().equalsIgnoreCase(value)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
