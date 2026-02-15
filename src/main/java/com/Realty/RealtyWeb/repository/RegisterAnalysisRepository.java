package com.Realty.RealtyWeb.repository;

import com.Realty.RealtyWeb.Entity.RegisterAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegisterAnalysisRepository extends JpaRepository<RegisterAnalysisEntity, Long> {

    List<RegisterAnalysisEntity> findAllByUserid(String userid);
}
