package com.Realty.RealtyWeb.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Purpose {
    원룸("원룸"),
    빌라("빌라"),
    오피스텔("오피스텔"),
    아파트("아파트"),
    상가("상가"),
    단독("단독"),
    다가구("다가구"),
    연립("연립"),
    기타("기타");

    private final String value;

    Purpose(String value) {
        this.value = value;
    }

    @JsonValue // Json 변환 시 한글 그대로 사용
    public String getValue() {
        return value;
    }

    @JsonCreator // Json 요청에서 한글을 Enum으로 변환
    public static Purpose fromValue(String value) {
        for (Purpose purpose : Purpose.values()) {
            if (purpose.value.equalsIgnoreCase(value)) {
                return purpose;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }

}


