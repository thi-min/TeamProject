package com.project.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//회원가입 요청
@Data
public class MemberSignUpRequestDto {
	
	@NotBlank(message = "한글만 입력하세요")
    private String memberName; //이름
	
	@NotBlank(message = "아이디는 필수입니다.")
	private String memberId; //이메일 아이디
	
	@NotBlank(message = "비밀번호는 필수입니다.")
    private String memberPw; //비밀번호
	@NotBlank(message = "비밀번호가 다릅니다.")
	private String memberPwCheck; //비밀번호 확인
	
    private String memberBirth; //생년월일
    private String memberPhone; //휴대폰 번호
    
    private String memberAddress; //주소
}
