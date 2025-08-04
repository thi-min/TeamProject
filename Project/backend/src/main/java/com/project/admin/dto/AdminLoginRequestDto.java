package com.project.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
//로그인 요청
public class AdminLoginRequestDto {
	//관리자 아이디
	@NotBlank(message = "아이디는 필수입니다.")
	private String adminId;
	
	@NotBlank(message = "비밀번호는 필수입니다.")
	private String adminPw;
}
