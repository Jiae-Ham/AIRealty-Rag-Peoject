package com.Realty.RealtyWeb.token;

import com.Realty.RealtyWeb.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private static final Map<String, String> refreshTokenStorage = new ConcurrentHashMap<>();

    public JwtTokenProvider(@Value("${security.jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 🔹 JWT 토큰 생성 (Access & Refresh Token)
     */
    public JwtToken generateToken(Authentication authentication) {
        String userId = authentication.getName();
        long now = System.currentTimeMillis();

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(userId)
                .claim("roles", getAuthorities(authentication))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 1000 * 60 * 60)) // 1시간
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 1000 * 60 * 60 * 24 * 7)) // 7일
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 저장
        storeRefreshToken(userId, refreshToken);

        return JwtToken.builder()
                .grantToken("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 🔹 Access Token 재발급 (Refresh Token 사용)
     */
    public JwtToken refreshToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        String userId = claims.getSubject();

        // 저장된 Refresh Token과 비교
        if (!refreshToken.equals(refreshTokenStorage.get(userId))) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        // Refresh Token 만료 여부 확인
        if (claims.getExpiration().before(new Date())) {
            throw new IllegalArgumentException("Refresh Token이 만료되었습니다.");
        }

        // Authentication 객체 생성 및 새로운 Access Token 발급
        Authentication authentication = getAuthentication(refreshToken);
        JwtToken newToken = generateToken(authentication);

        // 새로운 Refresh Token 저장
        storeRefreshToken(userId, newToken.getRefreshToken());

        return newToken;
    }

    /**
     * 🔹 Refresh Token 저장
     */
    public void storeRefreshToken(String userId, String refreshToken) {
        refreshTokenStorage.put(userId, refreshToken);
    }

    /**
     * 🔹 Refresh Token 무효화
     */
    public void revokeRefreshToken(String userId) {
        refreshTokenStorage.remove(userId);
    }

    /**
     * 🔹 토큰에서 인증 정보 추출
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (claims.get("roles") == null) {
            throw new RuntimeException("권한 정보가 없습니다.");
        }

        // 🔥 role 값이 변환되지 못하는 경우 발생
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("roles").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * 🔹 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 비어있습니다.");
        }
        return false;
    }

    /**
     * 🔹 토큰에서 Claims 추출
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * 🔹 권한 정보 추출
     */
    private String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}
