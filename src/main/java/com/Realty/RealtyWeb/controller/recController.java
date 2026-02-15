package com.Realty.RealtyWeb.controller;


import com.Realty.RealtyWeb.dto.HouseBoardSummaryDTO;
import com.Realty.RealtyWeb.dto.RecResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class recController {


    @GetMapping("/list")
    public List<HouseBoardSummaryDTO> crawl(@RequestParam("addrcode") String addrcode, @RequestParam("uid") String uid) {
        WebClient webClient = WebClient.create("http://localhost:3030");
        List<HouseBoardSummaryDTO> houseList = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/")
                        .queryParam("addrcode", addrcode)
                        .queryParam("uid", uid)
                        .build())
                .retrieve()
                .bodyToFlux(HouseBoardSummaryDTO.class) // Flux = 여러 개 (JSON 배열)
                .collectList()               // Flux → List
                .block();                    // 동기식으로 변환

        return houseList;
    }


}
