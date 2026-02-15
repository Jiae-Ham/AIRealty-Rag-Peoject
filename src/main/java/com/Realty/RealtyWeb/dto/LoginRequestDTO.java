package com.Realty.RealtyWeb.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@Data
public class LoginRequestDTO {
    private String userId;
    private String userPw;
}
