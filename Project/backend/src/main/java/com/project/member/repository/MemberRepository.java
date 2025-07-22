package com.project.member.repository;

import com.project.member.entity.MemberEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    // 로그인 시 사용: 아이디 + 비밀번호로 조회
    Optional<MemberEntity> findByMemberIdAndMemberPw(String memberId, String memberPw);

    // 아이디 중복 체크
    boolean existsByMemberId(String memberId);
    
    // 카카오 연동 회원 조회
    Optional<MemberEntity> findByKakaoId(String kakaoId);

    // 회원번호로 조회(mypage)
    Optional<MemberEntity> findByMemberNum(Long memberNum);

    // 연락처로 회원 조회 or 핸드폰번호 기준으로 회원 존재여부 확인
    Optional<MemberEntity> findByMemberPhone(String memberPhone);
    
    // 이름 검색 (관리자 전용)
    Page<MemberEntity> findByMemberNameContaining(String name, Pageable pageable);
    
    //아이디 찾기
    Optional<MemberEntity> findByID(String MemberName, String memberPhone);
    
    //비밀번호 찾기
    Optional<MemberEntity> findByPassword(String MemberId, String MemberName, String memberPhone);
    
    //비밀번호 변경
    Optional<MemberEntity> changeByPassword(String MemberId);
    
}

//Optional 데이터가 있을수도 없을수도있음.