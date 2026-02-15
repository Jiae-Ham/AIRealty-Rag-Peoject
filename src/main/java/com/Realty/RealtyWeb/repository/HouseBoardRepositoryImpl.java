package com.Realty.RealtyWeb.repository;

import com.Realty.RealtyWeb.Entity.HouseBoardEntity;
import com.Realty.RealtyWeb.Entity.HouseInfoEntity;
import com.Realty.RealtyWeb.dto.HouseBoardFilterDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class HouseBoardRepositoryImpl implements HouseBoardRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<HouseBoardEntity> findAllByFilter(HouseBoardFilterDTO filter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // 메인 쿼리 생성
        CriteriaQuery<HouseBoardEntity> cq = cb.createQuery(HouseBoardEntity.class);
        Root<HouseBoardEntity> root = cq.from(HouseBoardEntity.class);
        // houseInfo와 한 번만 조인
        Join<HouseBoardEntity, HouseInfoEntity> infoJoin = root.join("houseInfo", JoinType.INNER);

        Predicate[] predicates = buildPredicates(filter, cb, infoJoin);

        cq.select(root)
                .where(predicates)
                .orderBy(cb.desc(root.get("createdAt")));

        TypedQuery<HouseBoardEntity> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Count 쿼리 생성 (동일한 조건 재사용)
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<HouseBoardEntity> countRoot = countQuery.from(HouseBoardEntity.class);
        Join<HouseBoardEntity, HouseInfoEntity> countInfoJoin = countRoot.join("houseInfo", JoinType.INNER);
        countQuery.select(cb.count(countRoot))
                .where(buildPredicates(filter, cb, countInfoJoin));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(query.getResultList(), pageable, totalCount);
    }

    private Predicate[] buildPredicates(HouseBoardFilterDTO filter, CriteriaBuilder cb, Join<HouseBoardEntity, HouseInfoEntity> infoJoin) {
        List<Predicate> predicates = new ArrayList<>();

        // 매물 종류
        if (filter.getPurpose() != null) {
            predicates.add(cb.equal(infoJoin.get("purpose"), filter.getPurpose()));
        }

        // 지역 코드
        if (filter.getAddrCode() != null) {
            predicates.add(cb.equal(infoJoin.get("addrCode"), filter.getAddrCode()));
        }

        // 거래 방식
        if (filter.getTransactionType() != null) {
            predicates.add(cb.equal(infoJoin.get("transactionType"), filter.getTransactionType()));
        }

        // 가격 필터링 (최소 ~ 최대)
        if (filter.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(infoJoin.get("price"), filter.getMinPrice()));
        }
        if (filter.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(infoJoin.get("price"), filter.getMaxPrice()));
        }

        // 전용 면적 필터링
        if (filter.getMinExclusiveArea() != null) {
            predicates.add(cb.greaterThanOrEqualTo(infoJoin.get("exclusiveArea"), filter.getMinExclusiveArea()));
        }
        if (filter.getMaxExclusiveArea() != null) {
            predicates.add(cb.lessThanOrEqualTo(infoJoin.get("exclusiveArea"), filter.getMaxExclusiveArea()));
        }

        // 월세 필터링
        if (filter.getMinRentPrc() != null) {
            predicates.add(cb.greaterThanOrEqualTo(infoJoin.get("rentPrc"), filter.getMinRentPrc()));
        }

        if (filter.getMaxRentPrc() != null) {
            predicates.add(cb.lessThanOrEqualTo(infoJoin.get("rentPrc"), filter.getMaxRentPrc()));
        }

        // 주차 대수 필터링
        if (filter.getMinParkingPerHouseholdCount() != null) {
            predicates.add(cb.greaterThanOrEqualTo(infoJoin.get("parkingPerHouseholdCount"), filter.getMinParkingPerHouseholdCount()));
        }

        /*
        // 층수 필터링
        if (filter.getMinFloor() != null) {
            predicates.add(cb.greaterThanOrEqualTo(infoJoin.get("floor"), filter.getMinFloor()));
        }
        if (filter.getMaxFloor() != null) {
            predicates.add(cb.lessThanOrEqualTo(infoJoin.get("floor"), filter.getMaxFloor()));
        }

        // 사용 승인일 (builtYear)
        if (filter.getBuiltYear() != null) {
            predicates.add(cb.equal(infoJoin.get("builtYear"), filter.getBuiltYear()));
        }

        // 반려동물 허용 여부
        if (filter.getPetAllowed() != null) {
            predicates.add(cb.equal(infoJoin.get("pet"), filter.getPetAllowed()));
        }

        // 주차 가능 여부
        if (filter.getParkingAvailable() != null) {
            predicates.add(cb.equal(infoJoin.get("parking"), filter.getParkingAvailable()));
        }
*/
        return predicates.toArray(new Predicate[0]);
    }
}
