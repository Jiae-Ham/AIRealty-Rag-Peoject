package com.Realty.RealtyWeb.repository;

import com.Realty.RealtyWeb.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUserId(String userId);
    Optional<UserEntity> findByUserEmail(String userEmail);
    Optional<UserEntity> findByUserName(String userName);
    Optional<UserEntity> findByUserNameAndUserIdAndUserPhone(String username, String userid, String userphone);
}