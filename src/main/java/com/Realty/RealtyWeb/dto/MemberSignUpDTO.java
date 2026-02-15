package com.Realty.RealtyWeb.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSignUpDTO {
    private String userId;
    private String userPw;
    private String userName;
    private String userPhone;
    private String userEmail;

}
