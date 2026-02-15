package com.Realty.RealtyWeb.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "wishlist", uniqueConstraints = @UniqueConstraint(columnNames = {"userid", "pid"}))
public class WishlistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 위시리스트 ID (자동 증가)

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private UserEntity user;  // 찜한 사용자

    @ManyToOne
    @JoinColumn(name = "pid", nullable = false)
    private HouseBoardEntity houseBoard;  // 찜한 매물 게시글

    @Column(nullable = false)
    private LocalDateTime addedAt; // 찜한 날짜

    @PrePersist
    public void prePersist() {
        this.addedAt = LocalDateTime.now();
    }
}
