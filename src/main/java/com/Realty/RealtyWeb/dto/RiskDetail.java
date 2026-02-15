package com.Realty.RealtyWeb.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RiskDetail {
    private String keyword;
    private String level;
    private String title;
    private String description;
}
