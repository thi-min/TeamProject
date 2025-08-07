package com.project.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//로그인 완료 후 front-end 에 제공할 변수 미리 선언
public class MemberLoginResponseDto {
	//공통 로그인 정보
    private String memberId;       // 로그인 ID (일반: 이메일, 카카오: kakaoId)
    private String memberName;     // 이름
    private String message;        // 응답 메시지 (선택)
    private String accessToken;    // JWT Access Token
    private String refreshToken;   // JWT Refresh Token

    //소셜 로그인 사용자 추가 정보 (카카오용)
    private boolean requireSignup; // 신규 가입 필요 여부
    private String kakaoId;        // 카카오 고유 ID
    private String email;          // 카카오 이메일
    private String gender;         // male / female
    private String birth;          // 생년월일 (yyyy-MM-dd)
    private String phone;          // 01012345678 ← 아래에서 가공 처리
}

