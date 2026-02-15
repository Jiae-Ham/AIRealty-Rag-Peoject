package com.Realty.RealtyWeb.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
    private String userId;
    private String userName;
    private String userEmail;
    private String userPhone;
}
