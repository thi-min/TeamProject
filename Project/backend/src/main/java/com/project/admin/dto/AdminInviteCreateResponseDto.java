package com.project.admin.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * 관리자 초대 생성 "응답" DTO
 * - 발급된 초대 토큰과 만료시각을 클라이언트로 반환
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AdminInviteCreateResponseDto {

    /** 1회용 초대 토큰 문자열(서버가 생성) — 가입 시 제출해야 하는 핵심 값 */
    private String token;

    /** 토큰 만료 시각 — 이 시간이 지나면 토큰이 무효 처리됨 */
    private LocalDateTime expiresAt;
}
