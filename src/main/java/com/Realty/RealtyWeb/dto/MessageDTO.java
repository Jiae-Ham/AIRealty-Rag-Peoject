package com.Realty.RealtyWeb.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private ROLE role;
    private String content;

    public enum ROLE {
        system, user, assistant
    }

}
