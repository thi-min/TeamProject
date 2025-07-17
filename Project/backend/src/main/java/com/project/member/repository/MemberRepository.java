package com.project.member.repository;

public class MemberRepository {
	
	//로그인 시 사용할 아이디, 비밀번호로 조회
	Optional<MemberEntity> findBytMemberIdAndMemberPw(String memberId, String memberPw);
	
	//아이디 중복 체크
	boolean existsByMemberId(String memberId);
}
