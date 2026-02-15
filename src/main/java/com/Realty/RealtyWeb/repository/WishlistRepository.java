package com.Realty.RealtyWeb.repository;

import com.Realty.RealtyWeb.Entity.HouseBoardEntity;
import com.Realty.RealtyWeb.Entity.UserEntity;
import com.Realty.RealtyWeb.Entity.WishlistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishlistEntity, Long> {
    // 특정 사용자의 찜 목록 조회
    List<WishlistEntity> findByUser(UserEntity user);

    // 특정 사용자가 특정 매물을 찜했는지 확인
    Optional<WishlistEntity> findByUserAndHouseBoard(UserEntity user, HouseBoardEntity houseBoard);
}
