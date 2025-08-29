package com.project.admin.service;

import java.util.Optional;

import com.project.admin.dto.AdminLoginRequestDto;
import com.project.admin.dto.AdminLoginResponseDto;
import com.project.admin.dto.AdminMemberDetailResponseDto;
import com.project.admin.dto.AdminMemberListResponseDto;
import com.project.admin.dto.AdminMemberUpdateRequestDto;
import com.project.admin.dto.AdminPasswordUpdateRequestDto;
import com.project.admin.entity.AdminEntity;
import com.project.member.dto.MemberPageRequestDto;
import com.project.member.dto.MemberPageResponseDto;
import com.project.member.entity.MemberState;

public interface AdminService {
    
    //관리자 로그인
    //@param - 로그인요청 - AdminLoginRequestDto
    //@return - 로그인응답 - AdminLoginResponseDto
    AdminLoginResponseDto login(AdminLoginRequestDto dto);
    
    //회원정보수정(관리자)
    //회원상태변경 - 정상, 휴먼, 탈퇴
    //회원계정잠금 - true, false
    //AdminMemberStateChangeDto adminMemberStateChange(Long memberNum, MemberState memberState);
    AdminMemberUpdateRequestDto adminMemberStateChange(Long memberNum, MemberState memberState, Boolean memberLock);
    
    //회원 목록 조회 (관리자용) 
    //여러명의 회원(각각 기본키 보유)을 한번에 조회하기 떄문에 List사용
    //페이징 + 검색까지 구현
    MemberPageResponseDto<AdminMemberListResponseDto> getMemberList(MemberPageRequestDto req);

    
    //회원 정보 상세 조회(관리자용)
    AdminMemberDetailResponseDto adminMemberDetailView(Long memberNum);
    
    //회원 정보 수정(관리자용)
    //AdminMemberDateUpdateRequestDto adminMemberUpdateView(Long memberNum);
    
    //관리자 비밀번호 변경
    void updatePassword(String adminId, AdminPasswordUpdateRequestDto dto);
	
    //관리자 ID로 관리자 정보를 찾는 메서드 추가
    Optional<AdminEntity> findByAdminId(String adminId);
    
}