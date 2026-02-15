package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.dto.AddrCodeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AddrCodeService {
    @Value("${crawl.cookie}")
    private String cookie;

    @Value("${crawl.auth}")
    private String auth;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://new.land.naver.com")
            .defaultHeader("Cookie", cookie) // 헤더 설정
            .defaultHeader("Authorization", auth)
            .defaultHeader("Accept-Language", "ko-KR,ko;q=0.9")
            .defaultHeader("Sec-Ch-Ua", "\"Not A(Brand\";v=\"8\", \"Chromium\";v=\"132\"")
            .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36")
            .defaultHeader("Accept", "*/*")
            .defaultHeader("Sec-Fetch-Site", "same-origin")
            .defaultHeader("Sec-Fetch-Mode", "cors")
            .defaultHeader("Referer", "https://new.land.naver.com/complexes/791?ms=37.5848635,126.9203881,17&a=APT&b=B1:B2:A1&e=RETAIL")
            .defaultHeader("Accept-Encoding", "gzip, deflate, br")
            .defaultHeader("Priority", "u=1, i")
            .build();;

    public List<AddrCodeResponseDTO> getAddrCode(String code) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/regions/list")
                        .queryParam("cortarNo", code)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(body -> {
                    List<?> regionList = (List<?>) body.get("regionList");
                    return regionList.stream()
                            .map(item -> convertMapToDto((Map<String, Object>) item))
                            .toList();
                })
                .block();
    }
    private AddrCodeResponseDTO convertMapToDto(Map<String, Object> map) {
        return new AddrCodeResponseDTO(
                (String) map.get("cortarNo"),
                Double.parseDouble(map.get("centerLat").toString()),
                Double.parseDouble(map.get("centerLon").toString()),
                (String) map.get("cortarName"),
                (String) map.get("cortarType")
        );
    }
}
