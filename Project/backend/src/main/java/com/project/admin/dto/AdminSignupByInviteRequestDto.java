package com.project.admin.dto;

import lombok.*;

/**
 * 초대 수락(=관리자 회원가입) "요청" DTO
 * - 발급받은 초대 토큰과 함께 관리자 계정 정보를 제출
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AdminSignupByInviteRequestDto {

    /** 초대 토큰(필수) — 서버가 검증(유효/만료/미사용) 후 진행 */
    private String token;

    /** 생성할 관리자 아이디 (로그인용 ID) — 중복 불가 */
    private String adminId;

    /** 관리자 이메일 — 초대 토큰의 targetEmail과 일치해야 함(권장/필수 정책) */
    private String adminEmail;

    /** 관리자 비밀번호 — 서버에서 암호화(PasswordEncoder) 저장 */
    private String adminPw;

    /** 관리자 이름(실명) — Audit/표시용 */
    private String adminName;

    /** 관리자 휴대폰 번호 — 연락/2차 인증 등 용도 */
    private String adminPhone;

    /**
     * (선택) 기존 Member와 연동할 때 사용되는 회원 번호
     * - null 가능
     * - 연동 정책이 있을 경우 MemberEntity 조회/매핑
     */
    private Long memberNum;
}
