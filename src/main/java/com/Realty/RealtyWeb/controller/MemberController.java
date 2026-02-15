package com.Realty.RealtyWeb.controller;

import com.Realty.RealtyWeb.dto.MemberSignUpDTO;
import com.Realty.RealtyWeb.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.Realty.RealtyWeb.services.MemberService;
import com.Realty.RealtyWeb.dto.MemberDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    //회원가입
    //multipart 적용
    @PostMapping(value = "/join")
    public ResponseEntity<?> join(
            @RequestBody MemberSignUpDTO signUpDTO) {
        try {
            MemberDTO member = memberService.join(signUpDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(member);
        } catch (IllegalArgumentException e) {
            // ⚠ 클라이언트가 알 수 있도록 400 에러와 메시지 전달
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    //id 중복체크
    @PostMapping("/checkId/{userId}")
    public ResponseEntity<Boolean> checkId(@PathVariable String userId) {
        return ResponseEntity.ok(!memberService.checkId(userId));
    }

    //마이페이지 이동
    @GetMapping("/my-page/{userId}")
    public ResponseEntity<MemberDTO> myPage(@PathVariable String userId) {
        return memberService.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    //비밀번호 찾기 페이지 이동
    @PostMapping("/find-password")
    public ResponseEntity<String> findPassword(@RequestBody Map<String, String> request) {
        String userName = request.get("userName");
        String userId = request.get("userId");
        String userPhone = request.get("userPhone");

       try {
           String tempPassword = memberService.findPassword(userName, userId, userPhone);
           return ResponseEntity.ok("임시 비밀번호가 발급되었습니다." + tempPassword);
       }catch (IllegalArgumentException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원 정보를 찾을 수 없습니다.");
       }

    }

    //비밀번호 변경
    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        boolean success = memberService.updatePassword(userId, oldPassword, newPassword);
        if (success) {
            return ResponseEntity.ok("비밀번호 변경 향상됩니다.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("기존 비밀번호가 일치하지 않습니다.");
    }



    //회원 리스트 이동
    @GetMapping("/list")
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        return ResponseEntity.ok(memberService.findByAllMember());
    }

    //회원정보 수정
    @PutMapping("/update")
    public ResponseEntity<MemberDTO> update(@RequestBody MemberDTO memberDTO) {
        return ResponseEntity.ok(memberService.update(memberDTO));
    }

    // 회원 삭제
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteMember(@PathVariable String userId, @RequestParam String userPw) {
        try {
            boolean success = memberService.delete(userId, userPw);
            if (success) {
                return ResponseEntity.ok("성공적으로 삭제되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete member");
            }
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect password");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found");
        }
    }

}