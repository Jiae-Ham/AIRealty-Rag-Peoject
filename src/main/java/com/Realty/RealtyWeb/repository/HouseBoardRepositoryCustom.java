package com.Realty.RealtyWeb.repository;

import com.Realty.RealtyWeb.Entity.HouseBoardEntity;
import com.Realty.RealtyWeb.dto.HouseBoardFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HouseBoardRepositoryCustom {
    Page<HouseBoardEntity> findAllByFilter(HouseBoardFilterDTO filter, Pageable pageable);
}
