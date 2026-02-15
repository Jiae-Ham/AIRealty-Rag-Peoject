package com.Realty.RealtyWeb.controller;

import com.Realty.RealtyWeb.token.JwtToken;
import com.Realty.RealtyWeb.services.AuthService;
import com.Realty.RealtyWeb.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginInfo) {
        String userId = loginInfo.get("userId");
        String userPw = loginInfo.get("userPw");

        JwtToken token = authService.login(userId, userPw);
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", token.getAccessToken());
        response.put("refreshToken", token.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    // Refresh Token으로 Access Token 재발급 -- 나중에 수정 필요
    @PostMapping("/token/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        JwtToken newToken = authService.refreshAccessToken(refreshToken);

        if (newToken != null) {
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newToken.getAccessToken());
            response.put("refreshToken", newToken.getRefreshToken());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
        }
    }


    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token format");
        }

        // 공백을 제거하고 실제 토큰만 추출
        String token = authorizationHeader.replace("Bearer ", "").trim();

        try {
            String userId = jwtTokenProvider.getAuthentication(token).getName();
            jwtTokenProvider.revokeRefreshToken(userId);
            return ResponseEntity.ok("로그아웃 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }
}
