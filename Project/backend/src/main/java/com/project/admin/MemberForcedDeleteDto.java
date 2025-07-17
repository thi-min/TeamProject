package com.project.admin;

import lombok.Data;

@Data
//회원 강제 탈퇴처리
public class MemberForcedDeleteDto {
	private Long memberNum;
    private String reason; // 삭제 사유(선택)
}
