package com.Realty.RealtyWeb.repository;

import com.Realty.RealtyWeb.Entity.HouseBoardEntity;
import com.Realty.RealtyWeb.Entity.HouseInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseInfoRepository extends JpaRepository<HouseInfoEntity, Long> {
    // 특정 게시글 ID(pid)에 해당하는 HouseInfo 조회
    Optional<HouseInfoEntity> findByHouseBoardEntity(HouseBoardEntity houseBoardEntity);
}
