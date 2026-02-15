package com.Realty.RealtyWeb.dto;

import com.Realty.RealtyWeb.Entity.HouseInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseInfoDTO {
    private Long houseId;        // 매물 ID
    private Long pid;            // 게시글 ID
    private String ownerType; // 집주인, 세입자
    private String purpose;      // 용도
    private String transactionType; // 거래 유형
    private BigDecimal price;        // 가격
    private BigDecimal maintenanceFee; // 관리비
    private String address;      // 주소
    private String addressDetail;// 상세 주소
    private BigDecimal exclusiveArea;// 전용 면적
    private BigDecimal supplyArea;   // 공급 면적
    private Integer rooms;       // 방 개수
    private Integer bathrooms;   // 욕실 개수
    private String direction;    // 방향
    private String houseDetail;  // 상세 설명

    private BigDecimal rentPrc; // 월세
    private BigDecimal parkingPerHouseholdCount; // 주차 대수
    private BigDecimal latitude; // 위도
    private BigDecimal longitude; // 경도

    /*
    private String buildingName; // 건물 이름
    private String builtYear;    // 준공 연도
    private String loanAvailable;// 대출 가능 여부
    private String pet;          // 반려동물 가능 여부
    private String parking;      // 주차 가능 여부
    private Integer floor;       // 층수
*/
    public static HouseInfoDTO fromEntity(HouseInfoEntity houseInfoEntity) {
        return HouseInfoDTO.builder()
                .houseId(houseInfoEntity.getHouseId())
                .pid(houseInfoEntity.getHouseBoardEntity().getPid())
                .ownerType(houseInfoEntity.getOwnerType().name())
                //.buildingName(houseInfoEntity.getBuildingName())
                .purpose(houseInfoEntity.getPurpose().name())
                .transactionType(houseInfoEntity.getTransactionType().name())
                .price(houseInfoEntity.getPrice())
                .maintenanceFee(houseInfoEntity.getMaintenanceFee())
                .address(houseInfoEntity.getAddress())
                .addressDetail(houseInfoEntity.getAddressDetail())
                .exclusiveArea(houseInfoEntity.getExclusiveArea())
                .supplyArea(houseInfoEntity.getSupplyArea())
                .rooms(houseInfoEntity.getRooms())
                .bathrooms(houseInfoEntity.getBathrooms())
                //.floor(houseInfoEntity.getFloor())
                .direction(houseInfoEntity.getDirection().name())
                //.builtYear(houseInfoEntity.getBuiltYear())
                //.loanAvailable(houseInfoEntity.getLoanAvailable().name())
                //.pet(houseInfoEntity.getPet().name())
                //.parking(houseInfoEntity.getParking().name())
                .houseDetail(houseInfoEntity.getHouseDetail())
                .rentPrc(houseInfoEntity.getRentPrc())
                .parkingPerHouseholdCount(houseInfoEntity.getParkingPerHouseholdCount())
                .longitude(houseInfoEntity.getLongitude())
                .latitude(houseInfoEntity.getLatitude())
                .build();
    }
}
