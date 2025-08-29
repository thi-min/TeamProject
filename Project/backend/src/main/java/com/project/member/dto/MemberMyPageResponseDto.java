package com.project.member.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.member.entity.MemberSex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//마이페이지 
//사용자 정보를 보여주기 위한 dto
public class MemberMyPageResponseDto {
	private String memberName; //사용자 이름
    private String memberId; //로그인 id
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate memberBirth; //생년월일
    
    private MemberSex memberSex; //성별
    
    private String memberPostcode;        // 우편번호
    private String memberRoadAddress;     // 기본주소(도로명/지번)
    private String memberDetailAddress;   // 상세주소
    private String memberAddress; //주소(전체표출)
    
    private String memberPhone; //연락처
    private String kakaoId; //카카오 계정
    private boolean smsAgree; // SNS 인증 여부
}