package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.dto.HouseBoardDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WishlistService {
    // 매물 찜하기
    boolean toggleWishlist(UserDetails userDetails, Long pid);

    // 특정 사용자의 찜한 매물 목록 조회
    List<HouseBoardDTO> getWishlist(String userId);


}
