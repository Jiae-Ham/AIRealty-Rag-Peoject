package com.Realty.RealtyWeb.controller;

import com.Realty.RealtyWeb.dto.*;
import com.Realty.RealtyWeb.services.CodefRegisterService;
import com.Realty.RealtyWeb.services.HouseInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/codef")
@RequiredArgsConstructor
public class CodefRegisterController {

    private final CodefRegisterService codefRegisterService;
    private final HouseInfoService houseInfoService;


    // 1차 요청: 기본 입력으로 등기부 조회 시도
//    @PostMapping("/register")
//    public ResponseEntity<JsonNode> requestRegister(@RequestBody CodefRequestDTO dto) throws JsonProcessingException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, InterruptedException {
//
//        JsonNode node = codefRegisterService.requestRegisterFirst(dto, houseInfoService.findAddressByPid(dto.getPid()));
//
//        // PDF/바이너리인 경우: binaryData 필드만 존재
//        if (node.has("binaryData")) {
//            return ResponseEntity.ok(node);    // 프론트에서 다운로드 처리
//        }
//        return ResponseEntity.ok(node);        // 정상 JSON
//    }

    // 고유번호 단일조회 - 테스트용
    @PostMapping("/unique")
    public ResponseEntity<JsonNode> byUnique(@RequestBody UniqueNoRequestDTO dto)
            throws JsonProcessingException {

        JsonNode res = codefRegisterService.requestByUnique(dto);

        // ▸ PDF 라면 binaryData 래퍼, JSON 은 그대로
        return ResponseEntity.ok(res);
    }



}
