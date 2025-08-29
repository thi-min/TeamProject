package com.project.admin.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.member.entity.MemberSex;
import com.project.member.entity.MemberState;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
//특정 회원 상세 정보
public class AdminMemberDetailResponseDto {
	private Long memberNum; //회원 번호
	private String memberId; //아이디
	private String memberName; //이름
	private LocalDate memberBirth; //생년월일 yyyy-mm-dd
	private String memberPhone; //핸드폰 번호
	private String memberAddress; //주소
	private String memberDay; //가입일시
	private boolean memberLock; //계정 잠금여부(true/flase)
	private boolean smsAgree; //sns 수신여부    
    private MemberState memberState; //회원상태("ACTIVE","REST","OUT")
	private MemberSex memberSex; //성별
}
