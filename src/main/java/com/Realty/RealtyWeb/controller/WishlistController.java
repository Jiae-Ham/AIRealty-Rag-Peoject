package com.Realty.RealtyWeb.controller;

import com.Realty.RealtyWeb.dto.HouseBoardDTO;
import com.Realty.RealtyWeb.services.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    // 찜하기/찜 취소 (토글)
    @PostMapping("/{pid}/toggle")
    public ResponseEntity<String> toggleWishlist(
            @PathVariable Long pid,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean isAdded = wishlistService.toggleWishlist(userDetails, pid);

        return ResponseEntity.ok(isAdded ? "찜 추가" : "찜 삭제");

    }

    // 특정 사용자의 찜한 매물 목록 조회
    @GetMapping
    public ResponseEntity<List<HouseBoardDTO>> getUserWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        List<HouseBoardDTO> wishlist = wishlistService.getWishlist(userId);

        return ResponseEntity.ok(wishlist);
    }
}
