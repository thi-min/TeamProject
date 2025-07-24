package com.project.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

//회원가입 응답(완료창)
@Data
@AllArgsConstructor
public class MemberSignUpResponseDto {
	private String memberId; //이메일 아이디
	private String message; //메시지
	
	//추후 가입한 아이디를 포함한 데이터를 프론트에서 구현할 예정
}
