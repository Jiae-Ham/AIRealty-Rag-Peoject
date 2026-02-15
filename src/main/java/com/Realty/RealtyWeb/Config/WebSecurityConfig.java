package com.Realty.RealtyWeb.Config;


import com.Realty.RealtyWeb.token.JwtAuthenticationFilter;
import com.Realty.RealtyWeb.token.JwtExceptionFilter;
import com.Realty.RealtyWeb.token.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig{

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                config.setAllowedMethods(Collections.singletonList("*"));
                config.setAllowCredentials(true);
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setMaxAge(3600L);
                return config;
            }
        }));
            http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests((requests) -> requests
                            .requestMatchers("/images/**").permitAll()
                            .requestMatchers("/api/member/join").permitAll()
                            .requestMatchers("/api/auth/login").permitAll() //공개 경로 이따가 적어둬야함.
                            .requestMatchers("/api/auth/token/refresh").permitAll()
                            .requestMatchers("/api/member/list").permitAll()
                            .requestMatchers("/api/member/find-password").permitAll()
                            .requestMatchers("/api/codef/register").permitAll()
                            .requestMatchers("/api/codef/register/2way").permitAll()
                            .requestMatchers("/api/codef/register/auto").permitAll()
                            .requestMatchers("/api/codef/unique").permitAll()

                            .requestMatchers("/api/analysis/**").authenticated()
                            .requestMatchers("/api/analysis/**").authenticated()
                            .requestMatchers("/api/Realty/chat").authenticated()
                            .requestMatchers("/api/house-board/**").authenticated()
                            .requestMatchers("/api/house-info/**").authenticated()
                            .requestMatchers("/api/member/my-page/**").authenticated()
                            .requestMatchers("/member/update-password").authenticated()
                            .requestMatchers("/api/member/update").authenticated()
                            .requestMatchers("/api/member/delete").authenticated()
                            .anyRequest().authenticated() //그 외 요청은 인증 필요
                    )
                    .formLogin(form -> form.disable())
                    .logout(logout -> logout.permitAll())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

    http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class);
            return http.build();


    }

}
