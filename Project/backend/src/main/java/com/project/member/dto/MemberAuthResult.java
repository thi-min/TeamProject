package com.project.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 인증 통과 후, 토큰 발급 전에 Controller로 넘겨줄 최소 정보 DTO
 * - 토큰/리프레시 저장은 Controller에서만 수행
 */
@Getter
@Builder
@AllArgsConstructor
public class MemberAuthResult {
    private final Long memberNum;
    private final String memberId;
    private final String memberName;
}