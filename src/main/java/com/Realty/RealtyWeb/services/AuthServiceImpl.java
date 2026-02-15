package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.Entity.UserEntity;
import com.Realty.RealtyWeb.repository.UserRepository;
import com.Realty.RealtyWeb.token.JwtToken;
import com.Realty.RealtyWeb.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public JwtToken login(String userId, String userPw) {
        UserEntity user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userPw, user.getUserPw())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public void logout(String userId) {
        jwtTokenProvider.revokeRefreshToken(userId);
    }

    @Override
    public JwtToken refreshAccessToken(String refreshToken) {
        return jwtTokenProvider.refreshToken(refreshToken);
    }
}
