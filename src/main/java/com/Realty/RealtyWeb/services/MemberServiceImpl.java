package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.Entity.UserEntity;
import com.Realty.RealtyWeb.dto.MemberDTO;
import com.Realty.RealtyWeb.dto.MemberSignUpDTO;
import com.Realty.RealtyWeb.repository.MemberRepository;
import com.Realty.RealtyWeb.repository.UserRepository;
import com.Realty.RealtyWeb.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public boolean checkId(String userId) {
        return memberRepository.existsById(userId);
    }

    //회원가입
    @Override
    public MemberDTO join(MemberSignUpDTO signUpDTO) {
        if (checkId(signUpDTO.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }else if (checkName(signUpDTO.getUserName())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }else if (checkEmail(signUpDTO.getUserEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String hashedPw = passwordEncoder.encode(signUpDTO.getUserPw());

        // Entity 변환 후 저장
        UserEntity userEntity = UserEntity.builder()
                .userId(signUpDTO.getUserId())
                .userPw(hashedPw)
                .userName(signUpDTO.getUserName())
                .userPhone(signUpDTO.getUserPhone())
                .userEmail(signUpDTO.getUserEmail())
                .build();

        memberRepository.save(userEntity);

        //회원가입 후, 클라이언트에 반환할 DTO
        return new MemberDTO(
                signUpDTO.getUserId(),
                signUpDTO.getUserName(),
                signUpDTO.getUserEmail(),
                signUpDTO.getUserPhone()
        );
    }

    //특정 회원 조회
    @Override
    public Optional<MemberDTO> findByUserId(String userId) {
        return memberRepository.findByUserId(userId)
                .map(member -> MemberDTO.builder()
                        .userId(member.getUserId())
                        .userName(member.getDisplayName())
                        .userEmail(member.getUserEmail())
                        .userPhone(member.getUserPhone())
                        .build());
    }

    //모든 회원 조회
    @Override
    public List<MemberDTO> findByAllMember() {
        return memberRepository.findAll()
                .stream()
                .map(member -> MemberDTO.builder()
                        .userId(member.getUserId())
                        .userName(member.getDisplayName())
                        .userEmail(member.getUserEmail())
                        .userPhone(member.getUserPhone())
                        .build())
                .toList(); //Stream -> List
    }


    //회원 삭제
    @Override
    public boolean delete(String userId, String userPw) {
        return memberRepository.findByUserId(userId).map(member -> {
            // 입력된 비밀번호가 DB 비밀번호와 일치하는지 검증
            if (!passwordEncoder.matches(userPw, member.getUserPw())) {
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
            }

            //회원 삭제 전에 토큰 삭제
            jwtTokenProvider.revokeRefreshToken(userId);

            // 회원 탈퇴 (데이터 삭제)
            memberRepository.delete(member);
            return true;
        }).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }


    // 이메일 중복 체크 - 필요한가?
    @Override
    public boolean checkEmail(String userEmail) {
        return memberRepository.findByUserEmail(userEmail).isPresent();
    }

    //사용자 이름 중복 체크 - 이것도 필요한가?
    @Override
    public boolean checkName(String userName) {
        return memberRepository.findByUserName(userName).isPresent();
    }

    // 회원정보 수정
    @Override
    public MemberDTO update(MemberDTO memberDTO) {
        return memberRepository.findByUserId(memberDTO.getUserId()).map(user -> {
            user.setUserName(memberDTO.getUserName());
            user.setUserEmail(memberDTO.getUserEmail());
            user.setUserPhone(memberDTO.getUserPhone());
            UserEntity updatedUser = memberRepository.save(user);

            return MemberDTO.builder()
                    .userId(updatedUser.getUserId())
                    .userName(updatedUser.getDisplayName())
                    .userEmail(updatedUser.getUserEmail())
                    .userPhone(updatedUser.getUserPhone())
                    .build();
        }).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }


    //비밀번호 변경
    @Override
    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        return memberRepository.findByUserId(userId).map(member -> {
            // 기존 비밀번호 검증
            if (!passwordEncoder.matches(oldPassword, member.getUserPw())) {
                throw new BadCredentialsException("기존 비밀번호가 일치하지 않습니다.");
            }

            // 새 비밀번호 암호화 후 저장
            member.setUserPw(passwordEncoder.encode(newPassword));
            memberRepository.save(member);
            return true;
        }).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    // 비밀번호 찾기
    @Override
    public String findPassword(String username, String userid, String userphone) {
        UserEntity user = memberRepository.findByUserNameAndUserIdAndUserPhone(username, userid, userphone)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String tempPassword = generateTempPassword();

        //임시 비밀번호 암호화 후 저장
        user.setUserPw(passwordEncoder.encode(tempPassword));
        memberRepository.save(user);
        return tempPassword;
    }

    private String generateTempPassword() {
        int length = 8;
        String charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder tempPassword = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = (int) (charPool.length() * Math.random());
            tempPassword.append(charPool.charAt(index));
        }

        return tempPassword.toString();
    }
}