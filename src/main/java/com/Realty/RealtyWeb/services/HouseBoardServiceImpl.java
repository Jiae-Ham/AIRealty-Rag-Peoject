package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.Entity.HouseBoardEntity;
import com.Realty.RealtyWeb.Entity.HouseInfoEntity;
import com.Realty.RealtyWeb.Entity.UserEntity;
import com.Realty.RealtyWeb.dto.*;

import com.Realty.RealtyWeb.enums.*;
import com.Realty.RealtyWeb.repository.HouseBoardRepository;
import com.Realty.RealtyWeb.repository.HouseInfoRepository;
import com.Realty.RealtyWeb.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseBoardServiceImpl implements HouseBoardService {

    private final HouseBoardRepository houseBoardRepository;
    private final HouseInfoRepository houseInfoRepository;
    private final MemberRepository memberRepository;

    // 매물 등록
    @Transactional
    @Override
    public HouseResisterRequestDTO createHouseBoard(String userId, HouseBoardDTO houseBoardDTO, HouseInfoDTO houseInfoDTO) {
        // 회원 정보 조회
        UserEntity user = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다."));
        // 게시글 엔티티 생성
        HouseBoardEntity houseBoard = HouseBoardEntity.builder()
                .writer(user)
                .ptitle(houseBoardDTO.getPtitle())
                .content(houseBoardDTO.getContent())
                .pimg(houseBoardDTO.getPimg())
                .views(0) // 기본값 0
                .build();

        // 게시글 저장
        houseBoardRepository.save(houseBoard);

        // 매물 정보 엔티티 생성
        HouseInfoEntity houseInfo = HouseInfoEntity.builder()
                .houseBoardEntity(houseBoard)
                .ownerType(OwnerType.valueOf(houseInfoDTO.getOwnerType()))
                .purpose(Purpose.valueOf(houseInfoDTO.getPurpose()))
                .transactionType(TransactionType.valueOf(houseInfoDTO.getTransactionType()))
                .price(houseInfoDTO.getPrice())
                .maintenanceFee(houseInfoDTO.getMaintenanceFee())
                .address(houseInfoDTO.getAddress())
                .addressDetail(houseInfoDTO.getAddressDetail())
                .exclusiveArea(houseInfoDTO.getExclusiveArea())
                .supplyArea(houseInfoDTO.getSupplyArea())
                .rooms(houseInfoDTO.getRooms())
                .bathrooms(houseInfoDTO.getBathrooms())
                .direction(Direction.valueOf(houseInfoDTO.getDirection()))
                .houseDetail(houseInfoDTO.getHouseDetail())
                .rentPrc(houseInfoDTO.getRentPrc())
                .parkingPerHouseholdCount(houseInfoDTO.getParkingPerHouseholdCount())
                .longitude(houseInfoDTO.getLongitude())
                .latitude(houseInfoDTO.getLatitude())
                .build();

        // 매물 정보 저장
        houseInfoRepository.save(houseInfo);

        return HouseResisterRequestDTO.fromEntity(houseBoard, houseInfo);
    }

    // 특정 매물 게시글 조회
    @Override
    @Transactional
    public Optional<HouseResisterRequestDTO> getHouseBoardById(Long pid) {
       return houseBoardRepository.findById(pid)
               .map(houseBoardEntity -> {
                   houseBoardEntity.setViews(houseBoardEntity.getViews() + 1); //조회수 증가
                   houseBoardRepository.save(houseBoardEntity);

                   //info 조회
                   HouseInfoEntity houseInfoEntity = houseInfoRepository.findByHouseBoardEntity(houseBoardEntity)
                           .orElseThrow(() -> new IllegalArgumentException("해당 매물 정보가 없습니다."));

                   return HouseResisterRequestDTO.fromEntity(houseBoardEntity, houseInfoEntity);
               });
    }

    // ✅ 매물 게시글 목록 조회 (페이징 + 필터 적용)
    @Override
    @Transactional
    public Page<HouseBoardSummaryDTO> getAllHouseBoards(HouseBoardFilterDTO filter, Pageable pageable) {
        Page<HouseBoardEntity> houseBoards = houseBoardRepository.findAllByFilter(filter, pageable);
        return houseBoards.map(board -> {
            HouseInfoEntity houseInfo = houseInfoRepository.findByHouseBoardEntity(board)
                    .orElseThrow(() -> new IllegalArgumentException("해당 매물 정보가 없습니다."));
            return HouseBoardSummaryDTO.fromEntity(board, houseInfo);
        });
    }

    // 특정 회원이 작성한 매물 게시글 조회
    @Override
    public List<HouseResisterRequestDTO> getHouseBoardsByUsername(String username) {
        UserEntity user = memberRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임의 회원을 찾을 수 없습니다."));
        return houseBoardRepository.findByWriter(user)
                .stream()
                .map(board -> {
                    HouseInfoEntity houseInfo = houseInfoRepository.findByHouseBoardEntity(board)
                            .orElseThrow(() -> new IllegalArgumentException("해당 매물 정보가 없습니다."));
                    return HouseResisterRequestDTO.fromEntity(board, houseInfo);
                })
                .collect(Collectors.toList());
    }

    @Override
    public HouseResisterRequestDTO updateHouseBoard(Long pid, String userId, HouseBoardDTO houseBoardDTO, HouseInfoDTO houseInfoDTO) {
        HouseBoardEntity houseBoardEntity = houseBoardRepository.findById(pid)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매물 게시글입니다."));

        // 🔹 수정 권한 확인: 요청한 userId가 게시글 작성자와 일치하는지 검증
        if (!houseBoardEntity.getWriter().getUserId().equals(userId)) {
            throw new AccessDeniedException( "매물 게시글 수정 권한이 없습니다.");
        }

        // 게시글 정보 업데이트
        houseBoardEntity.setPtitle(houseBoardDTO.getPtitle());
        houseBoardEntity.setContent(houseBoardDTO.getContent());
        houseBoardEntity.setPimg(houseBoardDTO.getPimg());
        houseBoardRepository.save(houseBoardEntity);

        // 매물 정보 업데이트
        HouseInfoEntity houseInfoEntity = houseInfoRepository.findByHouseBoardEntity(houseBoardEntity)
                .orElseThrow(() -> new IllegalArgumentException("매물 정보가 존재하지 않습니다."));

        //houseInfoEntity.setBuildingName(houseInfoDTO.getBuildingName());
        houseInfoEntity.setPurpose(Purpose.valueOf(houseInfoDTO.getPurpose()));
        houseInfoEntity.setTransactionType(TransactionType.valueOf(houseInfoDTO.getTransactionType()));
        houseInfoEntity.setPrice(houseInfoDTO.getPrice());
        houseInfoEntity.setMaintenanceFee(houseInfoDTO.getMaintenanceFee());
        houseInfoEntity.setAddress(houseInfoDTO.getAddress());
        houseInfoEntity.setAddressDetail(houseInfoDTO.getAddressDetail());
        houseInfoEntity.setExclusiveArea(houseInfoDTO.getExclusiveArea());
        houseInfoEntity.setSupplyArea(houseInfoDTO.getSupplyArea());
        houseInfoEntity.setRooms(houseInfoDTO.getRooms());
        houseInfoEntity.setBathrooms(houseInfoDTO.getBathrooms());
        //houseInfoEntity.setFloor(houseInfoDTO.getFloor());
        houseInfoEntity.setDirection(Direction.valueOf(houseInfoDTO.getDirection()));
        //houseInfoEntity.setBuiltYear(houseInfoDTO.getBuiltYear());
        //houseInfoEntity.setLoanAvailable(LoanAvailability.valueOf(houseInfoDTO.getLoanAvailable()));
        //houseInfoEntity.setPet(PetAvailability.valueOf(houseInfoDTO.getPet()));
        //houseInfoEntity.setParking(ParkingAvailability.valueOf(houseInfoDTO.getParking()));
        houseInfoEntity.setRentPrc(houseInfoDTO.getRentPrc());
        houseInfoEntity.setParkingPerHouseholdCount(houseInfoDTO.getParkingPerHouseholdCount());
        houseInfoEntity.setLongitude(houseInfoDTO.getLongitude());
        houseInfoEntity.setLatitude(houseInfoDTO.getLatitude());
        houseInfoEntity.setHouseDetail(houseInfoDTO.getHouseDetail());
        houseInfoRepository.save(houseInfoEntity);

        return HouseResisterRequestDTO.fromEntity(houseBoardEntity, houseInfoEntity);

    }
    //게시글 삭제

    @Override
    public boolean deleteHouseBoard(Long pid, String userId) {
        // 게시글 찾기
        HouseBoardEntity houseBoard = houseBoardRepository.findById(pid)
                .orElseThrow(() -> new RuntimeException("해당 매물 게시글을 찾을 수 없습니다."));

        // 작성자 확인
        if (!houseBoard.getWriter().getUserId().equals(userId)) {
            throw new RuntimeException("해당 매물 게시글을 삭제할 권한이 없습니다.");
        }

        // 삭제
        houseBoardRepository.delete(houseBoard);
        return true;
    }
}


