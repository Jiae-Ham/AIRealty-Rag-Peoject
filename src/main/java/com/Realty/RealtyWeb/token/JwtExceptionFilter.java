package com.Realty.RealtyWeb.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 다음 필터로 이동
            filterChain.doFilter(request, response);
        } catch (RuntimeException ex) {
            // 예외 발생 시 로그 출력 및 응답 처리
            log.error("JWT 처리 중 예외 발생: {}", ex.getMessage());

            // HTTP 응답 상태와 내용을 설정
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            // 예외 메시지를 JSON 형태로 응답
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", ex.getMessage());

            // JSON 응답 작성
            String jsonResponse = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(errorResponse);
            response.getWriter().write(jsonResponse);
        }
    }
}
