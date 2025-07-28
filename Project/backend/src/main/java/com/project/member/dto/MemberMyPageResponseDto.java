package com.project.member.dto;

import java.time.LocalDate;

import com.project.member.entity.MemberSex;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
//마이페이지 
//사용자 정보를 보여주기 위한 dto
public class MemberMyPageResponseDto {
	private String memberName; //사용자 이름
    private String memberId; //로그인 id
    private String memberPw; //비밀번호
    private LocalDate memberBirth; //생년월일
    private MemberSex memberSex; //성별
    private String memberAddress; //주소
    private String memberPhone; //연락처
    private String kakaoId; //카카오 계정
    private boolean snsYn; // SNS 인증 여부
}