package com.project.member.dto;

import java.time.LocalDate;

import com.project.member.entity.MemberSex;

import lombok.Data;

@Data
public class KakaoSignUpRequestDto {

    private String kakaoId;           // 카카오 ID → memberId로 사용
    private String memberName;        // 이름
    private LocalDate memberBirth;    // 생년월일
    private String memberPhone;       // 01012345678 형식
    private String memberAddress;     // 주소
    private MemberSex memberSex;      // 성별 (MAN, WOMAN)
    private boolean smsAgree;         // 문자 수신 동의 여부
    
}