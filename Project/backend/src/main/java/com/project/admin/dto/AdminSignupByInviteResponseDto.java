package com.project.admin.dto;

import lombok.*;

/**
 * 초대 수락(=관리자 회원가입) "응답" DTO
 * - 가입 완료된 관리자 정보(요약)와 처리 메시지 반환
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AdminSignupByInviteResponseDto {

    /** 생성된 관리자 아이디 */
    private String adminId;

    /** 생성된 관리자 이메일 */
    private String adminEmail;

    /** 처리 결과 메시지 */
    private String message;
}
