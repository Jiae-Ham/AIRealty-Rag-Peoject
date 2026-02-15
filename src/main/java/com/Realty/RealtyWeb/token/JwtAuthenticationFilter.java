package com.Realty.RealtyWeb.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider; //Jwt 토큰을 생성하고 유저 정보를 저장하고 토큰를 전송할 수 있습니다
    private static final AntPathMatcher matcher = new AntPathMatcher();

    // 공개 URL
    private static final List<String> PUBLIC_PATTERNS = List.of(
            "/api/member/join",
            "/api/auth/login",
            "/images/**",
            "/api/auth/token/refresh",
            "/api/member/list",
            "/api/member/find-password",
            "/api/codef/register",
            "/api/codef/register/2way",
            "/api/codef/register/auto",
            "/api/codef/unique"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);
        String uri = request.getRequestURI();
        log.info("[JwtAuthFilter] 요청 URI: {}", uri);

//        // 🔥 공개 URL은 무조건 통과
//        if (uri.startsWith("/api/member/join") ||
//                uri.startsWith("/api/auth/login") ||
//                uri.startsWith("/api/auth/token/refresh") ||
//                uri.startsWith("/api/member/list") ||
//                uri.startsWith("/api/member/find-password") ||
//                uri.startsWith("/api/codef/register") ||
//                uri.startsWith("/api/codef/register/2way") ||
//                uri.startsWith("/api/codef/register/auto")) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        boolean isPublic = PUBLIC_PATTERNS.stream()
                .anyMatch(pattern -> matcher.match(pattern, uri));

        if (isPublic) {
            filterChain.doFilter(request, response);
            return;
        }

        // 이외는 토큰 검증
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[JwtAuthFilter] 인증 객체 설정 완료: {}", authentication.getName());
            filterChain.doFilter(request, response); // 정상 통과
        } else {
            log.warn("[JwtAuthFilter] 유효하지 않은 토큰 또는 토큰 없음");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"JWT 토큰이 없거나 유효하지 않습니다.\"}");
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer "를 부분 제거하고 토큰 반환
        }
        return null;
    }

}
