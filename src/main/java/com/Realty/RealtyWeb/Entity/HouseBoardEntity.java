package com.Realty.RealtyWeb.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "house_board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseBoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;  // 게시글 ID (자동 증가)

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private UserEntity writer;  // 작성자 (Members 테이블과 연관)

    @Column(nullable = false, length = 255)
    private String ptitle;  // 게시글 제목

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;  // 게시글 내용

    @Column(length = 255)
    private String pimg;  // 이미지 URL

    @Column(nullable = false)
    private Integer views = 0;  // 조회수 (기본값 0)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;  // 작성일

    @OneToOne(mappedBy = "houseBoardEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HouseInfoEntity houseInfo;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();  // 자동 생성
    }
}
