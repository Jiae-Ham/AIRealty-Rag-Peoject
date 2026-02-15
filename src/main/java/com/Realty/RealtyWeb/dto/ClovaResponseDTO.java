package com.Realty.RealtyWeb.dto;

import lombok.*;
import org.aspectj.internal.lang.annotation.ajcDeclareEoW;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClovaResponseDTO {

    private Status status;
    private Result result;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Status {
        private int code;
        private String message;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Result {
        private MessageDTO message;
        private String stopReason;
        private int inputLength;
        private int outputLength;
        private long seed;
        private List<AiFilter> aiFilter;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AiFilter {
        private String groupName;
        private String name;
        private String score;
    }
}

