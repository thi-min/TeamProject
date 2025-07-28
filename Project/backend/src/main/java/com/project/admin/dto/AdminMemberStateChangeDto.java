package com.project.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor	
@AllArgsConstructor
@Builder
//회원 강제 탈퇴처리
public class AdminMemberStateChangeDto {
	private Long memberNum;
    private String message; //상태 변경 사유 선택
}
