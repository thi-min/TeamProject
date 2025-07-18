package com.project.member.service;

import java.util.List;

import com.project.member.dto.MemberForcedDeleteDto;
import com.project.member.dto.MemberListResponseDto;
import com.project.member.dto.MemberLoginRequestDto;
import com.project.member.dto.MemberLoginResponseDto;
import com.project.member.dto.MemberMyPageResponseDto;
import com.project.member.dto.MemberMyPageUpdateRequestDto;
import com.project.member.dto.MemberPasswordUpdateRequestDto;
import com.project.member.dto.MemberSignUpRequestDto;
import com.project.member.dto.MemberSignUpResponseDto;

//회원관련 서비스 인터페이스
//회원가입, 로그인, 마이페이지등
public interface MemberService {
	//회원가입
	//@param - 회원가입 요청 - MemberSignUpRequestDto
	//@return - 회원가입 응답 MemberSignUpResponseDto
    MemberSignUpResponseDto sigup(MemberSignUpRequestDto dto);
    
    //로그인
    //@param - 로그인요청 - MemberLoginRequestDto
    //@return - 로그인응답 - MemberLoginResponseDto
    MemberLoginResponseDto login(MemberLoginRequestDto dto);
    
    //마이페이지
    MemberMyPageResponseDto myPage(Long memberNum);
    
    //회원탈퇴
    MemberForcedDeleteDto memberOut(Long memberNum);
    
    // 회원 목록 조회 (관리자용)
    List<MemberListResponseDto> getMemberList();

    // 비밀번호 변경
    void updatePw(MemberPasswordUpdateRequestDto dto);
    
    //아이디 찾기
    String findMemberId(String memberName, String memberPhone);
    
    //비밀번호 찾기
    String findMemberPw(String memberId, String memberName, String memberPhone);
    
    //휴대폰 인증
    String findMemberPhone(String memberPhone, String memberNum);
}