package com.project.member.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberState;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    // 로그인 시 사용: 아이디 + 비밀번호로 조회
	Optional<MemberEntity> findByMemberIdAndMemberPw(String memberId, String memberPw);

    // 아이디 중복 체크
    boolean existsByMemberId(String memberId);
    
    // 카카오 연동 회원 조회
    Optional<MemberEntity> findByKakaoId(String kakaoId);

    // 회원번호로 조회(mypage)
    Optional<MemberEntity> findByMemberNum(Long memberNum);

    // 아이디로 조회
    Optional<MemberEntity> findByMemberId(String memberId);
    
    // 연락처로 회원 조회 or 핸드폰번호 기준으로 회원 존재여부 확인
    Optional<MemberEntity> findByMemberPhone(String memberPhone);
    
    //아이디 찾기
    Optional<MemberEntity> findByMemberNameAndMemberPhone(String memberName, String memberPhone);
    
    //비밀번호 찾기
    Optional<MemberEntity> findByMemberIdAndMemberNameAndMemberPhone(String memberId, String memberName, String memberPhone);
    
    //비밀번호 만료 스케줄러
    List<MemberEntity> findByPwUpdatedBefore(LocalDateTime expiryThreshold);
    
    //탈퇴일 기준으로 삭제 대상 회원 찾기
    List<MemberEntity> findByMemberStateAndOutDateBefore(MemberState state, LocalDateTime dateTime);
    
    //페이지네이션 회원목록
    //페이징 처리된 전체 회원목록 조회
    Page<MemberEntity> findAll(Pageable pageable);
    //검색 + 페이징(이름에 키워드 포함된 회원 조회)
    Page<MemberEntity> findByMemberNameContaining(String keyword, Pageable pageable);

    //카카오 ID가 존재하는지 체크
	boolean existsByKakaoId(String kakaoId);
	//카카오 고유 식별자(kakaoId)로 회원 1건 조회
	Optional<MemberEntity> findFirstByKakaoId(String kakaoId);

    //상태 기준 조회시 필요할떄 사용
    //Page<MemberEntity> findByMemberState(MemberState state, Pageable pageable);
	
	
}

//Optional 데이터가 있을수도 없을수도있음.