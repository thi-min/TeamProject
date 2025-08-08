package com.project.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
//전체사용자 목록+필터링
public class AdminMemberListResponseDto {
	private Long memberNum; //회원 번호
	private String memberId; //아이디
	private String memberName; //이름
	private String memberDay; //가입일시
	private String memberState; //회원상태("ACTIVE","REST","OUT")
	private boolean memberLock; //계정 잠금여부(true/flase)
}