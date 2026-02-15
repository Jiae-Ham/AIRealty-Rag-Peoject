package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface HouseBoardService {
    // 매물 게시글 등록
    HouseResisterRequestDTO createHouseBoard(String userId, HouseBoardDTO houseBoardDTO, HouseInfoDTO houseInfoDTO);

    // 특정 매물 게시글 조회
    Optional<HouseResisterRequestDTO> getHouseBoardById(Long pid);

    // 전체 매물 게시글 조회
    Page<HouseBoardSummaryDTO> getAllHouseBoards(HouseBoardFilterDTO filter, Pageable pageable);

    // 특정 회원이 작성한 매물 게시글 조회
    List<HouseResisterRequestDTO> getHouseBoardsByUsername(String username);

    // 매물 게시글 수정
    HouseResisterRequestDTO updateHouseBoard(Long pid, String userId, HouseBoardDTO houseBoardDTO, HouseInfoDTO houseInfoDTO);

    // 매물 게시글 삭제
    boolean deleteHouseBoard(Long pid, String userId);
}
