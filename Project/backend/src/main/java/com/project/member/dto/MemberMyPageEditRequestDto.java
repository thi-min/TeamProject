package com.project.member.dto;

public class MemberMyPageEditRequestDto {
	private String memberName;       // 수정 불가 - 받아도 무시
	private String memberPhone;      // 수정 가능
    private String memberAddress;    // 수정 가능
    private boolean smsAgree;        // ✔ 포함됨

}
