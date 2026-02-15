package com.Realty.RealtyWeb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ClovaRequestDTO {
    private ArrayList<MessageDTO> messages;
    private double topP;
    private double topK;
    private double temperature;
    @JsonProperty("maxTokens")
    private Integer maxTokens = 256;
    private double repeatPenalty;
    private List<String> stopBefore;

}