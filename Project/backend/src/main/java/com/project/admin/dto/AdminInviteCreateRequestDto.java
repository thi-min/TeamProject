package com.project.admin.dto;

import lombok.*;

/**
 * (슈퍼관리자 전용) 관리자 초대 생성 "요청" DTO
 * - 초대할 대상과 유효시간 등 입력값을 서버로 보냄
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AdminInviteCreateRequestDto {

    /** 초대 대상 이메일 (예: 사내 계정) — 초대 토큰에 기록되어 검증에 사용 */
    private String targetEmail;

    /** 초대 토큰 유효시간(분). null/0 이하이면 서비스에서 기본값(예: 30분) 적용 */
    private Integer ttlMinutes;

    /** (선택) 초대 메모 — 운영자가 참고할 설명 */
    private String memo;
}
