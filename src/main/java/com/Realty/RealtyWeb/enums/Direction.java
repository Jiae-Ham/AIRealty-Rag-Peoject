package com.Realty.RealtyWeb.enums;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Direction {
    동향("동향"),
    서향("서향"),
    남향("남향"),
    북향("북향"),
    남동향("남동향"),
    남서향("남서향"),
    북동향("북동향"),
    북서향("북서향");

    private final String value;

    Direction(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Direction fromValue(String value) {
        for (Direction direction : Direction.values()) {
            if (direction.getValue().equalsIgnoreCase(value)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }


}
