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

        // 1) 토큰 발급
        final String kakaoAccessToken = kakaoApiService.getAccessToken(code);

        // 2) 사용자 정보 조회
        final KakaoUserInfoDto info = kakaoApiService.getUserInfo(kakaoAccessToken);
        final String rawEmail  = info.getEmail();                                // 카카오가 내려준 이메일
        final String email     = rawEmail == null ? null : rawEmail.toLowerCase(); // 🔑 비교 안정성 위해 소문자 정규화
        final String kakaoId   = info.getKakaoId();

        log.info("[KakaoCallback] email={}, kakaoId={}", email, kakaoId);

        // 3) 기존 회원 조회: 이메일 우선 → 없으면 kakaoId로도 시도
        Optional<MemberEntity> found = Optional.empty();
        if (email != null && !email.isBlank()) {
            found = memberRepository.findByMemberId(email);
        }
        if (found.isEmpty() && kakaoId != null && !kakaoId.isBlank()) {
            // 스키마에 kakaoId 컬럼이 없다면, memberId==kakaoId 로 저장한 경우를 대비해 memberId로도 조회
            // (kakaoId 컬럼이 있다면 findByKakaoId 로 바꾸세요)
            Optional<MemberEntity> byMemberId = memberRepository.findByMemberId(kakaoId);
            if (byMemberId.isPresent()) {
                found = byMemberId;
            } else if (hasKakaoIdColumn()) {
                // ⚠️ kakaoId 전용 컬럼이 있는 프로젝트라면 아래 메서드를 MemberRepository에 추가하고 사용
                found = memberRepository.findByKakaoId(kakaoId);
            }
        }

        if (found.isPresent()) {
            // 4) 로그인 처리: JWT 발급 + HttpOnly 쿠키 세팅
            MemberEntity m = found.get();

            final String subject = m.getMemberId();              // 토큰 subject는 "로그인 ID(이메일)"로 통일
            final String role    = "USER";                       // 필요 시 m.getRole() 등에서 읽어오세요
            final String at = jwtTokenProvider.createAccessToken(subject, role);
            final String rt = jwtTokenProvider.createRefreshToken(subject, role);

            // HttpOnly 쿠키(도메인/secure/sameSite는 환경에 맞게 조정)
            ResponseCookie atCookie = ResponseCookie.from("accessToken", at)
                    .httpOnly(true)
                    .secure(false)             // HTTPS면 true 권장
                    .path("/")
                    .maxAge(Duration.ofMinutes(30))
                    .sameSite("Lax")
                    .build();
            ResponseCookie rtCookie = ResponseCookie.from("refreshToken", rt)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(Duration.ofDays(7))
                    .sameSite("Lax")
                    .build();

            Map<String, Object> body = new HashMap<>();
            body.put("action", "signin_ok");
            body.put("redirect", "/member/mypage"); // 프론트에서 이 경로로 navigate

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, atCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, rtCookie.toString())
                    .body(body);
        }

        // 5) 회원 없음 → /join 으로 보내기 위한 프리필 구성
        Map<String, Object> prefill = new HashMap<>();
        prefill.put("memberId", email != null ? email : "");          // 현재 정책: ID=이메일 (이전에 ID=카카오ID였다면 맞춰 수정)
        prefill.put("memberName", info.getName());
        prefill.put("memberBirth", toIsoBirth(info.getBirthyear(), info.getBirthday())); // YYYY-MM-DD
        prefill.put("memberSex", normalizeSex(info.getGender()));                       // MAN/WOMAN
        prefill.put("memberPhone", info.getPhoneNumber());                              // +82 ... → 프론트에서 포맷팅

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
