package com.project.member;

import lombok.Data;

@Data
//비밀번호 변경
// 1. 마이페이지 > 비밀번호변경 (memberNum 생략
// 2. 오래된 비밀번호 일경우(비밀번호가 만료된 상태라서 로그인x)
public class MemberPasswordUpdateRequestDto {
	private Long memberNum;     		//로그인 되어있으면 생략
	private String currentPassword;    // 현재 비밀번호(일반 비밀번호 변경시 사용)
	private String newPassword;        // 새 비밀번호
	private String UpdatePassword;    // 새 비밀번호 확인 (선택)
	
	private boolean isExpiredChange;   // 비밀번호 만료 변경인지 여부(요청구분용)
}


//공동객체 구조화 시키면 좋을듯
//메시지 + 상태