package com.project.phone.dto;


import lombok.*;

/**
 * 인증번호 전송 요청 DTO
 * purpose: "SIGNUP" | "UPDATE"
 * - UPDATE의 경우, 서버에서 로그인 사용자 ID를 읽는다면 requesterId는 없어도 되지만
 *   여기서는 명시적으로 받는 형태도 허용(필요 시 무시 가능)
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PhoneVerificationRequestDto {
    private String phoneNumber; // 회원이 입력한 전화번호(숫자만)
    private String purpose;     // "SIGNUP" or "UPDATE"
    private String requesterId; // (선택) 요청자 ID/이메일(UPDATE일 때 본인 확인용)
}