package com.project.member.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.common.jwt.JwtTokenProvider;
import com.project.member.dto.KakaoUserInfoDto;
import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberSex;
import com.project.member.repository.MemberRepository;
// ✅ 네가 가진 기존 서비스/DTO (패키지 경로 맞춤)
import com.project.member.service.KakaoApiService;

import lombok.RequiredArgsConstructor;

/**
 * 카카오 로그인 콜백 컨트롤러 (네 프로젝트의 KakaoApiService/KakaoUserInfoDto 시그니처에 맞춘 버전)
 *
 * 흐름:
 *  1) 프론트에서 전달한 code로 AccessToken(String) 발급: kakaoApiService.getAccessToken(code)
 *  2) AccessToken으로 사용자 정보 조회: kakaoApiService.getUserInfo(accessToken) → KakaoUserInfoDto
 *  3) DB 조회 (1순위: kakaoId, 2순위: email(memberId))
 *  4-1) 기존회원 → JWT 발급하여 로그인 응답
 *  4-2) 신규회원 → /join/sigup 프리필 데이터 반환 (이름/이메일/폰/생일/성별 + kakaoId)
 *
 * 응답(JSON):
 *  - LOGIN  : { status, accessToken, refreshToken, memberId, memberName }
 *  - SIGNUP : { status, kakaoId, prefill: { memberName, memberId, memberPhone, birth, sex } }
 *
 * 📌 정책 정리:
 *  - 가입/로그인용 memberId는 "카카오 계정 이메일(account_email)" 사용
 *  - kakaoId는 MemberEntity.kakaoId에 저장하는 "연동키"로만 사용 (로그인 ID로 쓰지 않음)
 */
@RestController
@RequestMapping("/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoApiService kakaoApiService;   // code→token / token→userinfo
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
        try {
            // 1) code → accessToken (String 반환)
            final String accessToken = kakaoApiService.getAccessToken(code);

            // 2) accessToken → KakaoUserInfoDto
            final KakaoUserInfoDto info = kakaoApiService.getUserInfo(accessToken);

            // ⬇ 네 DTO 필드명에 맞춰 정확히 매핑
            final String kakaoId = nullIfBlank(info.getKakaoId());       // 카카오 고유 ID (연동키)
            final String email   = nullIfBlank(info.getEmail());         // memberId로 사용할 이메일
            final String name    = nullIfBlank(info.getNickname());      // 이름
            final String phone   = normalizePhone(info.getPhoneNumber()); // "+82 10-1234-5678" → "01012345678"
            final LocalDate birth = toBirthDate(info.getBirthyear(), info.getBirthday()); // "YYYY"+"MMDD" → LocalDate
            final MemberSex sex  = toMemberSexEnum(info.getGender());    // "male"/"female" → MemberSex(예: MAN/WOMAN)

            // 3) DB 회원 조회: kakaoId 1순위 → email(memberId) 2순위
            Optional<MemberEntity> found = Optional.empty();
            if (kakaoId != null && !kakaoId.isBlank()) {
                // ❗ MemberRepository에 findFirstByKakaoId(String) 필요
                found = memberRepository.findFirstByKakaoId(kakaoId);
            }
            if (found.isEmpty() && email != null && !email.isBlank()) {
                // ❗ MemberRepository에 findByMemberId(String) 필요
                found = memberRepository.findByMemberId(email);
            }

            // 4-1) 기존 회원 → JWT 발급/로그인
            if (found.isPresent()) {
                MemberEntity member = found.get();

                // kakaoId가 비어 있으면 이번에 백필(연동키 저장)
                if ((member.getKakaoId() == null || member.getKakaoId().isBlank()) && kakaoId != null) {
                    member.setKakaoId(kakaoId);
                    memberRepository.save(member);
                }

                // 기존 JwtTokenProvider 규칙에 맞춰 발급 (role은 "USER" 가정)
                String at = jwtTokenProvider.createAccessToken(member.getMemberId(), "USER");
                String rt = jwtTokenProvider.createRefreshToken(member.getMemberId(), "USER");

                return ResponseEntity.ok(new LoginResult(
                        "LOGIN",
                        at,
                        rt,
                        member.getMemberId(),
                        member.getMemberName()
                ));
            }

            // 4-2) 신규 회원 → /join/sigup 프리필 데이터 반환
            return ResponseEntity.ok(new SignupPrefillResult(
                    "SIGNUP",
                    safe(kakaoId),
                    new Prefill(
                            safe(name),
                            safe(email),                 // memberId 로 사용할 이메일
                            safe(phone),
                            birth != null ? birth.toString() : "", // yyyy-MM-dd
                            sex != null ? sex.name() : ""          // Enum → "MAN"/"WOMAN" 등
                    )
            ));
        } catch (Exception e) {
            // 예외 응답(간단)
            return ResponseEntity.status(400).body(new ErrorResult(
                    "ERROR",
                    "카카오 로그인 처리 중 오류가 발생했습니다.",
                    e.getMessage()
            ));
        }
    }

    // ────────────────────── 유틸 ──────────────────────

    private String safe(String s) { return s == null ? "" : s; }

    private String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    /**
     * 카카오 전화번호 예: "+82 10-1234-5678" 또는 "+82-10-1234-5678"
     * → "01012345678" 로 정규화 (숫자만, 82는 0으로 교체)
     */
    private String normalizePhone(String raw) {
        if (raw == null) return null;
        String digits = raw.replaceAll("[^0-9]", "");
        if (digits.startsWith("82")) {
            digits = "0" + digits.substring(2);
        }
        return digits;
    }

    /**
     * birthyear: "1995", birthday: "0214"(MMDD) → LocalDate(1995-02-14)
     * 둘 중 하나라도 없으면 null
     */
    private LocalDate toBirthDate(String birthyear, String birthday) {
        if (birthyear == null || birthday == null || birthday.length() != 4) return null;
        try {
            String mm = birthday.substring(0, 2);
            String dd = birthday.substring(2, 4);
            return LocalDate.parse(birthyear + "-" + mm + "-" + dd);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * gender: "male" / "female" → MemberSex Enum으로 매핑
     * 네 Enum 값이 MAN/WOMAN 이라면 아래 그대로 사용.
     * 만약 M/F 라면 MemberSex.M / MemberSex.F 로 바꿔줘.
     */
    private MemberSex toMemberSexEnum(String kakaoGender) {
        if (kakaoGender == null) return null;
        switch (kakaoGender.toLowerCase()) {
            case "male":   return MemberSex.MAN;    // ← 프로젝트 Enum 값에 맞춰 수정
            case "female": return MemberSex.WOMAN;  // ← 프로젝트 Enum 값에 맞춰 수정
            default:       return null;
        }
    }

    // ─────────── 컨트롤러 내부 응답용(간단 DTO 대체) ───────────
    private record LoginResult(
            String status,
            String accessToken,
            String refreshToken,
            String memberId,
            String memberName
    ) {}

    private record SignupPrefillResult(
            String status,
            String kakaoId,
            Prefill prefill
    ) {}

    private record Prefill(
            String memberName,
            String memberId,   // 이메일 (memberId 로 사용)
            String memberPhone,
            String birth,      // yyyy-MM-dd
            String sex         // Enum name (예: MAN/WOMAN)
    ) {}

    private record ErrorResult(
            String status,
            String message,
            String detail
    ) {}
}
