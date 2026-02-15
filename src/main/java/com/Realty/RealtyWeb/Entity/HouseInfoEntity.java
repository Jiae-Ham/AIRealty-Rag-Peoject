package com.Realty.RealtyWeb.Entity;


import com.Realty.RealtyWeb.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "house_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "houseid", nullable = false)
    private Long houseId;  // 매물 ID (자동 증가)

    @OneToOne
    @JoinColumn(name = "pid", referencedColumnName = "pid", nullable = false)
    private HouseBoardEntity houseBoardEntity;  // 해당 게시글과 연결

    @Enumerated(EnumType.STRING)
    @Column(name = "ownership_type", nullable = false)
    private OwnerType ownerType;  // 집주인, 세입자

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Purpose purpose;  // 용도

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;  // 거래 유형

    @Column(nullable = false, columnDefinition = "DECIMAL(15,2)")
    private BigDecimal price;  // 가격

    @Column(name = "maintenance_fee", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal maintenanceFee;  // 관리비

    @Column(nullable = false, length = 255)
    private String address;  // 주소

    @Column(name = "address_detail", nullable = false, length = 255)
    private String addressDetail;  // 상세 주소

    @Column(name = "exclusive_area", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal exclusiveArea;  // 전용 면적 (㎡)

    @Column(name = "supply_area", nullable = false, columnDefinition = "DECIMAL(10,7)")
    private BigDecimal supplyArea;  // 공급 면적 (㎡)

    @Column
    private Integer rooms;  // 방 개수

    @Column
    private Integer bathrooms;  // 욕실 개수

    @Enumerated(EnumType.STRING)
    @Column
    private Direction direction;  // 방향 (동향, 서향 등)


    @Column
    private BigDecimal rentPrc; // 월세

    @Column(name = "parking_per_household_count", nullable = false, columnDefinition = "DECIMAL(5,7)")
    private BigDecimal parkingPerHouseholdCount; // 주차 대수

    @Column(name = "latitude", columnDefinition = "DECIMAL(10,6)")
    private BigDecimal latitude; // 위도

    @Column(name = "longitude", columnDefinition = "DECIMAL(10,6)")
    private BigDecimal longitude; // 경도



    @Column(name = "address_code", length = 30)
    private String addrCode; // 지역 코드



    /*

    @Column
    private Integer floor;  // 층수

    @Column(name = "building_name", nullable = false, length = 255)
    private String buildingName;  // 건물 이름

        @Column(name = "built_year", length = 255)
        private String builtYear;  // 준공 연도

        @Enumerated(EnumType.STRING)
        @Column(name = "loan_available", length = 10)
        private LoanAvailability loanAvailable;  // 대출 가능 여부

        @Enumerated(EnumType.STRING)
        @Column(length = 10)
        private PetAvailability pet;  // 반려동물 가능 여부

        @Enumerated(EnumType.STRING)
        @Column(length = 10)
        private ParkingAvailability parking;  // 주차 가능 여부
    */
    @Column(name = "house_detail", length = 255)
    private String houseDetail;  // 상세 설명
}
