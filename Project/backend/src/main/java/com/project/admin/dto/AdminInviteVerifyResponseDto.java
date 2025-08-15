package com.project.admin.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * 초대 토큰 "검증" 응답 DTO
 * - 토큰 유효 여부와 대상 이메일, 만료시각 등을 알려줌
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AdminInviteVerifyResponseDto {

    /** 토큰이 유효한지 여부 (true: 사용 가능, false: 무효/만료/사용됨) */
    private boolean valid;

    /** 토큰에 묶여 있는 대상 이메일 — 가입 시 입력 이메일과 일치해야 함 */
    private String targetEmail;

    /** 유효한 경우 만료 시각(표시용). 무효일 때는 null일 수 있음 */
    private LocalDateTime expiresAt;

    /** 부가 메시지 — 무효/만료 사유 또는 안내 문구 */
    private String message;
}
