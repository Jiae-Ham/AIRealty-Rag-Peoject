package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.Entity.HouseBoardEntity;
import com.Realty.RealtyWeb.Entity.UserEntity;
import com.Realty.RealtyWeb.Entity.WishlistEntity;
import com.Realty.RealtyWeb.dto.HouseBoardDTO;
import com.Realty.RealtyWeb.repository.HouseBoardRepository;
import com.Realty.RealtyWeb.repository.MemberRepository;
import com.Realty.RealtyWeb.repository.WishlistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final MemberRepository memberRepository;
    private final HouseBoardRepository houseBoardRepository;

    @Override
    @Transactional
    public boolean toggleWishlist(UserDetails userDetails, Long pid) {
        System.out.println("로그인된 사용자 : " + userDetails.getUsername()); // 아이디가 뜨네

        // 현재 로그인된 사용자 가져오기
        UserEntity user = memberRepository.findByUserId(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 매물 게시글 찾기
        HouseBoardEntity houseBoard = houseBoardRepository.findById(pid)
                .orElseThrow(() -> new RuntimeException("매물 게시글을 찾을 수 없습니다."));

        Optional<WishlistEntity> existingWishlist = wishlistRepository.findByUserAndHouseBoard(user, houseBoard);

        if (existingWishlist.isPresent()) {
            // 이미 찜한 경우 -> 삭제
            wishlistRepository.delete(existingWishlist.get());
            return false; // 찜 취소됨
        } else {
            // 찜하지 않은 경우 -> 추가
            WishlistEntity wishlist = WishlistEntity.builder()
                    .user(user)
                    .houseBoard(houseBoard)
                    .build();
            wishlistRepository.save(wishlist);
            return true; // 찜 추가됨
        }
    }


    @Override
    public List<HouseBoardDTO> getWishlist(String userId) {
        UserEntity user = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<WishlistEntity> wishlists = wishlistRepository.findByUser(user);

        return wishlists.stream()
                .map(wishlist -> HouseBoardDTO.fromEntity(wishlist.getHouseBoard()))
                .collect(Collectors.toList());
    }
}
