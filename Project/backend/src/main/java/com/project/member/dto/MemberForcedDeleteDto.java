package com.project.member.dto;

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
public class MemberForcedDeleteDto {
	private Long memberNum;
	private String memberName;
    private String message; // 삭제 사유(선택)
    
    @Override
    public String toString() {
        return "회원명: '" + memberName + "', 메시지: '" + message + "'";
    }
}
