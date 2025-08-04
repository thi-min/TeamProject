package com.project.member.service;

import com.project.member.dto.MemberDeleteDto;
import com.project.member.dto.MemberIdCheckResponseDto;
import com.project.member.dto.MemberLoginRequestDto;
import com.project.member.dto.MemberLoginResponseDto;
import com.project.member.dto.MemberMyPageResponseDto;
import com.project.member.dto.MemberMyPageUpdateRequestDto;
import com.project.member.dto.MemberPasswordUpdateRequestDto;
import com.project.member.dto.MemberSignUpRequestDto;
import com.project.member.dto.MemberSignUpResponseDto;
import com.project.member.entity.MemberEntity;

//회원관련 서비스 인터페이스
//회원가입, 로그인, 마이페이지등
public interface MemberService {
	//회원가입
	//@param - 회원가입 요청 - MemberSignUpRequestDto
	//@return - 회원가입 응답 MemberSignUpResponseDto
    MemberSignUpResponseDto sigup(MemberSignUpRequestDto dto);
    
    //아이디 중복체크
    MemberIdCheckResponseDto checkDuplicateMemberId(String memberId);
    
    //로그인
    //@param - 로그인요청 - MemberLoginRequestDto
    //@return - 로그인응답 - MemberLoginResponseDto
    MemberLoginResponseDto login(MemberLoginRequestDto dto);
    
    //마이페이지
    MemberMyPageResponseDto myPage(Long memberNum);
    
    //마이페이지 수정
    MemberMyPageResponseDto updateMyPage(Long memberNum, MemberMyPageUpdateRequestDto dto);

    //회원탈퇴
    MemberDeleteDto memberOut(Long memberNum);

    // 비밀번호 변경
    void updatePassword(MemberPasswordUpdateRequestDto dto);
    
    //아이디 찾기
    String findMemberId(String memberName, String memberPhone);
    
    //비밀번호 찾기
    String findMemberPw(String memberId, String memberName, String memberPhone);

    //휴대폰 번호 중복 확인
    String checkPhoneNumber(String phoneNum);
    
    //비밀번호 만료
	boolean isPasswordExpired(MemberEntity member);

}