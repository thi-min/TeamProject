// backend/src/main/java/com/project/member/dto/MemberLoginResponseDto.java
package com.project.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 로그인 완료 후 front-end 에 제공할 변수 선언
public class MemberLoginResponseDto {
	
    // ✅ 공통 로그인 정보
	private Long memberNum;	   // 회원번호
    private String memberId;       // 로그인 ID (일반: 이메일, 카카오: kakaoId)
    private String memberName;     // 이름
    private String message;        // 응답 메시지 (선택)
    private String accessToken;    // JWT Access Token
    private String refreshToken;   // JWT Refresh Token

    // ✅ 권한 정보 (프론트 분기용)
    // - "ADMIN" 또는 "USER" 값 사용
    // - AuthController.login()에서 판별 후 세팅
    private String role;
}
