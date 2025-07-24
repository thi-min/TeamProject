package com.project.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
//로그인 요청
public class LoginRequestDto {
	//관리자 아이디
	@NotBlank(message = "아이디는 필수입니다.")
	private Long adminId;
	
	@NotBlank(message = "이메일 아이디는 필수입니다.")
	private String id;
	
	@NotBlank(message = "비밀번호는 필수입니다.")
	private String pw;


}
