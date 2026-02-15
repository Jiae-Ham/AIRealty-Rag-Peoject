package com.Realty.RealtyWeb.repository;

import com.Realty.RealtyWeb.Entity.HouseBoardEntity;
import com.Realty.RealtyWeb.Entity.UserEntity;
import com.Realty.RealtyWeb.dto.HouseBoardFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HouseBoardRepository extends JpaRepository<HouseBoardEntity, Long> {

    Page<HouseBoardEntity> findAllByFilter(HouseBoardFilterDTO filter, Pageable pageable);

    // 특정 유저가 작성한 게시글 조회
    List<HouseBoardEntity> findByWriter(UserEntity userEntity);

}
