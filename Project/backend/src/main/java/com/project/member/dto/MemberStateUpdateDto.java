package com.project.admin;

import com.project.member.MemberState;

import lombok.Data;

@Data
//탈퇴 처리, 휴먼 처리
public class MemberStateUpdateDto {
	private Long memberNum;
    private MemberState memberState; //회원상태("ACTIVE","REST","OUT")
}
