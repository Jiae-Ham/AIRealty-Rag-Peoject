package com.Realty.RealtyWeb.services;

import com.Realty.RealtyWeb.dto.MemberDTO;
import com.Realty.RealtyWeb.dto.MemberSignUpDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MemberService {

    boolean checkId(String userId); // ID 중복 체크

    MemberDTO join(MemberSignUpDTO signUpDTO); // 회원가입

    Optional<MemberDTO> findByUserId(String userId); // 특정 회원 조회

    List<MemberDTO> findByAllMember(); // 모든 회원 조회

    boolean delete(String userId, String userPw); // 회원 탈퇴

    boolean checkEmail(String userEmail); // 이메일 중복 체크

    boolean checkName(String userName); // 사용자 이름 중복 체크

    MemberDTO update(MemberDTO memberDTO); // 회원정보 수정

    boolean updatePassword(String userId, String oldPassword, String newPassword); // 비밀번호 변경

    String findPassword(String username, String userid, String userphone); // 비밀번호 찾기
}
