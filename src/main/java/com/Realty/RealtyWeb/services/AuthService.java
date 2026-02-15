package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.token.JwtToken;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    JwtToken login(String userId, String userPw);
    void logout(String userId);
    JwtToken refreshAccessToken(String refreshToken);


}
