package com.project.member.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.member.dto.AddressUpdateRequestDto;
import com.project.member.dto.MemberAuthResult;
import com.project.member.dto.MemberDeleteDto;
import com.project.member.dto.MemberIdCheckResponseDto;
import com.project.member.dto.MemberLoginRequestDto;
import com.project.member.dto.MemberLoginResponseDto;
import com.project.member.dto.MemberMeResponseDto;
import com.project.member.dto.MemberMyPageResponseDto;
import com.project.member.dto.MemberMyPageUpdateRequestDto;
import com.project.member.dto.MemberSignUpRequestDto;
import com.project.member.dto.MemberSignUpResponseDto;
import com.project.member.dto.PhoneUpdateRequestDto;
import com.project.member.dto.ResetPasswordUpdateRequestDto;
import com.project.member.dto.SelfPasswordUpdateRequestDto;
import com.project.member.entity.MemberEntity;

//회원관련 서비스 인터페이스
//회원가입, 로그인, 마이페이지등
@Service
public interface MemberService {
	//회원가입
	//@param - 회원가입 요청 - MemberSignUpRequestDto
	//@return - 회원가입 응답 MemberSignUpResponseDto
    MemberSignUpResponseDto sigup(MemberSignUpRequestDto dto);
    
    //아이디 중복체크
    MemberIdCheckResponseDto checkDuplicateMemberId(String memberId);
    boolean isDuplicatedMemberId(String memberId);
    
    //로그인
    //@param - 로그인요청 - MemberLoginRequestDto
    //@return - 로그인응답 - MemberLoginResponseDto
    //사용자 로그인 인증 (상태/비번 검증만 수행)
    MemberAuthResult authenticate(MemberLoginRequestDto dto);
    //로그인
    //MemberLoginResponseDto login(MemberLoginRequestDto dto);
    
//    //재발급(아래 3번에서 차단)
//    TokenPair reissue(String refreshToken);           
    //마이페이지
    MemberMyPageResponseDto myPage(Long memberNum);
    //분리한 주소
    MemberMyPageResponseDto updateMyAddress(Long memberNum, AddressUpdateRequestDto dto);
    //마이페이지 수정
    MemberMyPageResponseDto updateMyPage(Long memberNum, MemberMyPageUpdateRequestDto dto);
    //휴대폰 번호 변경
    MemberMyPageResponseDto updateMyPhone(Long memberNum, PhoneUpdateRequestDto dto);
    //수신동의 변경
    MemberMyPageResponseDto updateMySmsAgree(Long memberNum, boolean smsAgree);
    
    //회원탈퇴
    //MemberDeleteDto memberOut(Long memberNum);

    //비밀번호 변경(회원 로그인시)
    void updatePasswordSelf(Long memberNum, SelfPasswordUpdateRequestDto dto);
    //비밀번호 변경(비밀번호 찾기시)
    void resetPassword(ResetPasswordUpdateRequestDto dto);
    boolean isPasswordExpired(MemberEntity member);
    //비밀번호 만료
	
    // 비밀번호 변경
    //void updatePassword(ResetPasswordUpdateRequestDto dto);
    
    //아이디 찾기
    String findMemberId(String memberName, String memberPhone);
    
    //비밀번호 찾기
    String findMemberPw(String memberId, String memberName, String memberPhone);

    //휴대폰 번호 중복 확인
    String checkPhoneNumber(String phoneNum);
    
    //내 정보 조회
	MemberMeResponseDto getMyInfo(String memberId);
    //회원탈퇴
    MemberDeleteDto memberOut(Long memberNum, String requesterId, String message);
	
    //카카오 인가코드(code)를 받아 로그인 처리
	MemberLoginResponseDto handleKakaoLogin(String code) throws Exception;


    //회원 번호로 회원 객체를 찾아 반환
    MemberEntity findByMemberNum(Long memberNum);
    
    //회원 id로 회원 객체를 찾아 반환
    Optional<MemberEntity> findByMemberId(String memberId);

}