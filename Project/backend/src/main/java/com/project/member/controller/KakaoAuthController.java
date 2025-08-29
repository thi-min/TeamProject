// KakaoAuthController.java
package com.project.member.controller;

import com.project.common.jwt.JwtTokenProvider;
import com.project.member.dto.KakaoUserInfoDto;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.member.service.KakaoApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoApiService kakaoApiService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/kakao/callback")
    public ResponseEntity<?> handleKakaoCallback(String code) throws Exception {
        log.info("[KakaoCallback] code={}", code);

        // 1) 인가코드 → 카카오 AccessToken
        final String kakaoAccessToken = kakaoApiService.getAccessToken(code);

        // 2) 카카오 사용자 정보 조회
        final KakaoUserInfoDto info = kakaoApiService.getUserInfo(kakaoAccessToken);
        final String rawEmail = info.getEmail(); // 카카오 이메일
        final String email = rawEmail == null ? null : rawEmail.toLowerCase(); // 비교 안정성 위해 소문자 정규화
        final String kakaoId = info.getKakaoId();

        log.info("[KakaoCallback] email={}, kakaoId={}", email, kakaoId);

        // 3) 기존 회원 조회: 이메일(memberId) 우선 → 없으면 kakaoId 로도 시도
        Optional<MemberEntity> found = Optional.empty();

        if (email != null && !email.isBlank()) {
            found = memberRepository.findByMemberId(email);
        }

        if (found.isEmpty() && kakaoId != null && !kakaoId.isBlank()) {
            // (A) 과거 정책: memberId == kakaoId 로 저장된 유저 대응
            Optional<MemberEntity> byMemberId = memberRepository.findByMemberId(kakaoId);
            if (byMemberId.isPresent()) {
                found = byMemberId;
            } else if (hasKakaoIdColumn()) {
                // (B) DB 스키마에 kakaoId 전용 컬럼이 있는 경우 (프로젝트에 맞게 Repository 메서드 추가 필요)
                // found = memberRepository.findByKakaoId(kakaoId);
            }
        }

        // 4) 로그인 처리
        if (found.isPresent()) {
            MemberEntity m = found.get();

            // 토큰 subject는 "로그인 ID(이메일)"로 통일
            final String subject = m.getMemberId();
            // 필요 시 DB role 사용: m.getRole() 등
            final String role = "USER";

            final String accessToken = jwtTokenProvider.createAccessToken(subject, role);
            final String refreshToken = jwtTokenProvider.createRefreshToken(subject, role);

            // HttpOnly 쿠키 세팅 (도메인/secure/sameSite는 환경에 맞춰 조정)
            ResponseCookie atCookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)
                    .secure(false)              // HTTPS 환경이면 true 권장
                    .path("/")
                    .maxAge(Duration.ofMinutes(30))
                    .sameSite("Lax")            // 프론트/백엔드 오리진이 다르면 None; Secure=true 필요
                    .build();

            ResponseCookie rtCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ofDays(7))
                    .sameSite("Lax")
                    .build();

            // ⚠️ 프론트 가드(localStorage)와도 호환되게 바디에도 토큰/기본정보 포함
            Map<String, Object> body = new HashMap<>();
            body.put("action", "signin_ok");
            body.put("redirect", "/member/mypage");
            body.put("accessToken", accessToken);
            body.put("refreshToken", refreshToken);
            body.put("memberId", subject);
            body.put("role", role);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, atCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, rtCookie.toString())
                    .body(body);
        }

        // 5) 회원 없음 → /join 로 보내기 위한 프리필 구성
        Map<String, Object> prefill = new HashMap<>();
        prefill.put("memberId", email != null ? email : ""); // 정책: ID=이메일
        prefill.put("memberName", info.getName());           // nickname X, name O
        prefill.put("memberBirth", toIsoBirth(info.getBirthyear(), info.getBirthday())); // YYYY-MM-DD
        prefill.put("memberSex", normalizeSex(info.getGender())); // MAN/WOMAN
        prefill.put("memberPhone", info.getPhoneNumber());       // +82 형식 → 프론트에서 포맷

        Map<String, Object> res = new HashMap<>();
        res.put("action", "go_join");
        res.put("via", "kakao");
        res.put("kakaoId", kakaoId);
        res.put("prefill", prefill);

        return ResponseEntity.ok(res);
    }
    private String toIsoBirth(String birthyear, String birthday) {
        String y = birthyear == null ? "" : birthyear.trim();
        String mmdd = birthday == null ? "" : birthday.trim();
        if (y.length() == 4 && mmdd.length() == 4) {
            return y + "-" + mmdd.substring(0, 2) + "-" + mmdd.substring(2, 4);
        }
        return "";
    }

    private String normalizeSex(String gender) {
        if (gender == null) return "";
        String g = gender.toLowerCase();
        if ("male".equals(g) || "m".equals(g)) return "MAN";
        if ("female".equals(g) || "f".equals(g)) return "WOMAN";
        return "";
    }

    /** 프로젝트에 kakaoId 전용 컬럼이 있는지 여부를 상황에 맞게 리턴하세요.
     *  없으면 false로 유지하고, findByMemberId(kakaoId)만 사용해도 됩니다. */
    private boolean hasKakaoIdColumn() {
        return false; // kakaoId 컬럼이 있으면 true 로 바꾸고, Repository 메서드도 추가
    }
}
