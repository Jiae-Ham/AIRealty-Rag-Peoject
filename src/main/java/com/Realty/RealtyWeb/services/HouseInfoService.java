package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.dto.HouseInfoDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface HouseInfoService {
    // 특정 매물 정보 조회
    Optional<HouseInfoDTO> getHouseInfoById(Long houseId);

    // 모든 매물 정보 조회
    List<HouseInfoDTO> getAllHouseInfos();

    HouseInfoDTO findAddressByPid(Long pid);

}
