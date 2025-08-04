package com.project.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
//로그인 완료 후 front-end 에 제공할 변수 미리 선언
public class MemberLoginResponseDto {
    private String memberId; //아이디
    private String memberName; //회원이름
    private String message; //내용
    private String accessToken; // JWT 또는 세션 기반이라면 포함
    private String refreshToken; // JWT 또는 세션 기반이라면 포함
    
    private String kakaoId;	//카카오 아이디
    private boolean requireSignup;	//카카오 로그인 사용자 확인
}