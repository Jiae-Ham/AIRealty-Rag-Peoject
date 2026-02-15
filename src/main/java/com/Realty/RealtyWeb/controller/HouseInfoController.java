package com.Realty.RealtyWeb.controller;

import com.Realty.RealtyWeb.dto.HouseInfoDTO;
import com.Realty.RealtyWeb.services.HouseInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/house-info")
public class HouseInfoController {

    private final HouseInfoService houseInfoService;

    // 특정 매물 정보 조회
    @GetMapping("/{houseId}")
    public ResponseEntity<HouseInfoDTO> getHouseInfoById(@PathVariable Long houseId) {
        Optional<HouseInfoDTO> houseInfo = houseInfoService.getHouseInfoById(houseId);
        return houseInfo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 전체 매물 정보 조회
    @GetMapping("/all")
    public ResponseEntity<List<HouseInfoDTO>> getAllHouseInfos() {
        return ResponseEntity.ok(houseInfoService.getAllHouseInfos());
    }
}
