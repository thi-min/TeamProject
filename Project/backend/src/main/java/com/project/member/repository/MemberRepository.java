package com.project.member.repository;

import com.project.member.entity.MemberEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    // 연락처로 회원 조회 or 핸드폰번호 기준으로 회원 존재여부 확인
    Optional<MemberEntity> findByMemberPhone(String memberPhone);
    
    //아이디 찾기
    Optional<MemberEntity> findByMemberNameAndMemberPhone(String MemberName, String memberPhone);
    
    //비밀번호 찾기
    Optional<MemberEntity> findByMemberIdAndMemberNameAndMemberPhone(String MemberId, String MemberName, String memberPhone);
    
    //비밀번호 변경
    //Optional<MemberEntity> findByMemberIdAndChangePw(String MemberId);

    //페이지네이션 회원목록
    //페이징 처리된 전체 회원목록 조회
    Page<MemberEntity> findAll(Pageable pageable);
    //검색 + 페이징(이름에 키워드 포함된 회원 조회)
    Page<MemberEntity> findByMemberNameContaining(String keyword, Pageable pageable);

    //상태 기준 조회시 필요할떄 사용
    //Page<MemberEntity> findByMemberState(MemberState state, Pageable pageable);
    
    //아이디 조회 테스트용
	Optional<MemberEntity> findByMemberId(String string);
}

//Optional 데이터가 있을수도 없을수도있음.