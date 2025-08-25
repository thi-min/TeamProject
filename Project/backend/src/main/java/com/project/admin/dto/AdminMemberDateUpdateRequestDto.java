package com.project.admin.dto;

import com.project.member.entity.MemberState;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
//관리자 회원정보 수정
public class AdminMemberDateUpdateRequestDto {
	private boolean memberLock; //계정 잠금여부(true/flase)   
    private MemberState memberState; //회원상태("ACTIVE","REST","OUT")  
}
