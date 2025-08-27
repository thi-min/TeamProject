package com.project.member.controller;

import com.project.common.jwt.JwtTokenProvider;
import com.project.member.dto.KakaoUserInfoDto;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.member.service.KakaoApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 카카오 로그인 콜백 컨트롤러
 *
 * 분기:
 *  - 회원 존재(O): 기존 로그인 로직과 동일하게 토큰 발급 → 프론트가 /member/mypage 로 이동
 *  - 회원 존재(X): /join 으로 이동할 수 있게 프리필 전체(kakaoId, email, name, gender, birthday, birthyear, phoneNumber) 반환
 *
 * ⚠ 주의
 *  - 팀 규칙: "카카오 회원가입 시 kakaoId를 memberId로 사용"
 *  - JwtTokenProvider는 프로젝트에 맞춘 createAccessToken/createRefreshToken 사용
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoApiService kakaoApiService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/kakao/callback")
    public ResponseEntity<?> handleKakaoCallback(@RequestParam("code") String code) {
        try {
            // 1) code → access_token
            final String kakaoAccessToken = kakaoApiService.getAccessToken(code);

            // 2) access_token → 사용자 정보 DTO
            final KakaoUserInfoDto profile = kakaoApiService.getUserInfo(kakaoAccessToken);

            // 팀 규칙: kakaoId = memberId
            final String kakaoId    = nz(profile.getKakaoId());
            final String email      = nz(profile.getEmail());
            final String name       = nz(profile.getName());
            final String gender     = nz(profile.getGender());     // "male"/"female"
            final String birthday   = nz(profile.getBirthday());   // "MMDD"
            final String birthyear  = nz(profile.getBirthyear());  // "YYYY"
            final String phone      = nz(profile.getPhoneNumber()); // "+82 10-...."

            // 3) 회원 존재 여부
            final MemberEntity member = memberRepository.findByMemberId(kakaoId).orElse(null);

            // ────────────────────────────────────────────────
            // (A) 회원 없음 → 회원가입 필요: 프리필 '모두' 내려줌
            // ────────────────────────────────────────────────
            if (member == null) {
                Map<String, Object> body = new HashMap<>();
                body.put("login", false);
                body.put("signupRequired", true);
                // ✅ 프리필 전체 전달 (요청하신 phone/birthday/birthyear 포함)
                body.put("kakaoId", kakaoId);
                body.put("email", email);
                body.put("name", name);
                body.put("gender", gender);
                body.put("birthday", birthday);
                body.put("birthyear", birthyear);
                body.put("phoneNumber", phone);
                return ResponseEntity.ok(body);
            }

            // ────────────────────────────────────────────────
            // (B) 회원 있음 → 즉시 로그인 처리 (프로젝트 로그인 포맷 준수)
            // ────────────────────────────────────────────────
            final String subject = member.getMemberId(); // JWT subject 규칙: 이메일(=ID) → 현재 kakaoId
            final String role    = "USER";               // 카카오는 일반 회원

            // 기존 로그인과 동일 포맷으로 토큰 발급
            final String accessToken  = jwtTokenProvider.createAccessToken(subject, role);
            final String refreshToken = jwtTokenProvider.createRefreshToken(subject, role);

            // RefreshToken은 HttpOnly 쿠키(7일)로 전달 (프로젝트 정책 맞춤)
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(false)  // 운영 https면 true 권장
                    .path("/")
                    .maxAge(Duration.ofDays(7))
                    .sameSite("Lax")
                    .build();

            Map<String, Object> body = new HashMap<>();
            body.put("login", true);
            body.put("signupRequired", false);
            body.put("accessToken", accessToken);
            body.put("memberId", subject);
            body.put("memberName", member.getMemberName());
            body.put("role", role);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(body);

        } catch (Exception e) {
            log.error("[KakaoCallback] error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Kakao callback error: " + e.getMessage());
        }
    }

    private String nz(String s) { return s == null ? null : s; }
}
