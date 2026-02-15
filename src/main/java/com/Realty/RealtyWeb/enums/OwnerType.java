package com.Realty.RealtyWeb.enums;

import lombok.Getter;

@Getter
public enum OwnerType {
    //집주인, 세입자
    집주인("집주인"),
    세입자("세입자");

    private final String value;

    OwnerType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OwnerType fromValue(String value) {
        for (OwnerType ownerType : OwnerType.values()) {
            if (ownerType.getValue().equalsIgnoreCase(value)) {
                return ownerType;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }


}
