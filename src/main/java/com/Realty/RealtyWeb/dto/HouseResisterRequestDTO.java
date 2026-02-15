package com.Realty.RealtyWeb.dto;

import com.Realty.RealtyWeb.Entity.HouseBoardEntity;
import com.Realty.RealtyWeb.Entity.HouseInfoEntity;
import lombok.*;


@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class HouseResisterRequestDTO {
    private String userName;
    private HouseBoardDTO houseBoardDTO;
    private HouseInfoDTO houseInfoDTO;

    //응답용
    public static HouseResisterRequestDTO fromEntity (HouseBoardEntity board, HouseInfoEntity info) {
        return HouseResisterRequestDTO.builder()
                .userName(board.getWriter().getDisplayName()) //userId 대신 username 반환
                .houseBoardDTO(HouseBoardDTO.fromEntity(board))
                .houseInfoDTO(HouseInfoDTO.fromEntity(info))
                .build();

    }
}
