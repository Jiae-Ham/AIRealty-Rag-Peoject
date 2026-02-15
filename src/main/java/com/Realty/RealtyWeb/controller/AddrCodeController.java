package com.Realty.RealtyWeb.controller;

import com.Realty.RealtyWeb.dto.AddrCodeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.Realty.RealtyWeb.services.AddrCodeService;

import java.util.List;

@RestController
@RequestMapping("/api/addrCode")
@RequiredArgsConstructor
public class AddrCodeController {

    private final AddrCodeService addrCodeService;

    // 지역 코드 받아오는 api
    @GetMapping("/list")
    public ResponseEntity<List<AddrCodeResponseDTO>> getCortarList(@RequestParam("code") String code) {
        List<AddrCodeResponseDTO> cortarList = addrCodeService.getAddrCode(code);
        return ResponseEntity.ok(cortarList);
    }

    // 크롤링 요청
    @GetMapping("/crawl")
    public String crawl(@RequestParam("code") String code) {
        WebClient webClient = WebClient.create("http://localhost:3000");
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/")
                        .queryParam("code", code)
                        .build())
                .retrieve()
                .bodyToMono(Void.class) // 응답을 무시
                .subscribe(); // 요청만 보내고 반응은 무시
        //System.out.println("크롤링 시작됨");
        return "done";
    }

}
