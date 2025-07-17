package com.project.member;

import jakarta.validation.constraints.NotBlank;

//로그인 요청 (사용자 > 관리자)
public class MemberLoginRequestDto {
	@NotBlank(message = "아이디는 필수입니다.")
	private String memberId;
	
	@NotBlank(message = "비밀번호는 필수입니다.")
	private String memberPw;
}
