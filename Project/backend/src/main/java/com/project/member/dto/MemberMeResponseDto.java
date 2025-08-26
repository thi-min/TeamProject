package com.project.member.dto;

import java.time.LocalDate;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//마이페이지 진입 시 "내 정보 조회" API 응답용
public class MemberMeResponseDto {
    private Long memberNum;     // 탈퇴 API에 넣을 키
    private String memberId;    // 이메일 ID
    private String memberName;  // 이름
    private String memberState; // ACTIVE/REST/OUT 등 (선택)
    
    //추가 선택사항(김강민)
    private String memberPhone;   //휴대폰번호
    private LocalDate memberBirth;   //생년월일
}