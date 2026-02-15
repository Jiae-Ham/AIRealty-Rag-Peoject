package com.Realty.RealtyWeb.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AddrCodeResponseDTO {
    private String cortarNo;
    private double centerLat;
    private double centerLon;
    private String cortarName;
    private String cortarType;
}
