package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.Entity.HouseInfoEntity;
import com.Realty.RealtyWeb.dto.HouseInfoDTO;
import com.Realty.RealtyWeb.repository.HouseBoardRepository;
import com.Realty.RealtyWeb.repository.HouseInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseInfoServiceImpl implements HouseInfoService {

    private final HouseInfoRepository houseInfoRepository;
    private final HouseBoardRepository houseBoardRepository;

    // 특정 매물 정보 조회(houseID 기준)
    @Override
    public Optional<HouseInfoDTO> getHouseInfoById(Long houseId) {
        return houseInfoRepository.findById(houseId)
                .map(HouseInfoDTO::fromEntity);
    }

    // 전체 매물 정보 조회
    @Override
    public List<HouseInfoDTO> getAllHouseInfos() {
        return houseInfoRepository.findAll().stream()
                .map(HouseInfoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public HouseInfoDTO findAddressByPid(Long pid) {
        return houseBoardRepository.findById(pid)
                .flatMap(houseBoard -> houseInfoRepository.findByHouseBoardEntity(houseBoard))
                .map(HouseInfoDTO::fromEntity) // ✅ DTO로 변환
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 pid입니다."));
    }


}
