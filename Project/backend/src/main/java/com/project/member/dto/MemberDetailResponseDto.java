package com.project.member.dto;

import java.time.LocalDate;

import com.project.member.entity.MemberSex;
import com.project.member.entity.MemberState;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
//특정 회원 상세 정보
public class MemberDetailResponseDto {
	private Long memberNum; //회원 번호
	private String memberId; //아이디
	private String memberName; //이름
	private String memberBirth; //생년월일
	private String memberPhone; //핸드폰 번호
	private String memberAddress; //주소
	private LocalDate memberDay; //가입일시
	private boolean memberLock; //계정 잠금여부(true/flase)
	private boolean snsYn; //sns 수신여부
	
    private MemberState memberState; //회원상태("ACTIVE","REST","OUT")
	private MemberSex memberSex; //성별
}
