package com.Realty.RealtyWeb.dto;
import com.Realty.RealtyWeb.Entity.HouseBoardEntity;
import jakarta.persistence.Id;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HouseBoardDTO {
    private Long pid;         // 게시글 ID
    private String userName;    // 작성자 ID
    private String ptitle;    // 게시글 제목
    private String content;   // 게시글 내용
    private String pimg;      // 이미지 URL
    private Integer views;    // 조회수
    private String createdAt; // 작성일

    public static HouseBoardDTO fromEntity(HouseBoardEntity houseBoardEntity) {
        return HouseBoardDTO.builder()
                .pid(houseBoardEntity.getPid())
                .userName(houseBoardEntity.getWriter().getDisplayName()) //userId 대신 username
                .ptitle(houseBoardEntity.getPtitle())
                .content(houseBoardEntity.getContent())
                .pimg(houseBoardEntity.getPimg())
                .views(houseBoardEntity.getViews())
                .createdAt(houseBoardEntity.getCreatedAt().toString())
                .build();
    }
}
