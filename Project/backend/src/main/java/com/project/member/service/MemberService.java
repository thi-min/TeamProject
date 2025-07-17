package com.project.member.service;

import org.springframework.stereotype.Service;

import com.project.member.dto.MemberSignUpRequestDto;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service //tjqltmrPcmd(spring bean)으로 등록
@RequiredArgsConstructor //final로 선언된 memberRepository를 자동으로 생성자 주입 시켜줌
public class MemberService {
	
	//DB에 접근하는 인터페이스
	//회원가입시 저장, 중복 ID체크 DB관련 작업 수행
	private final MemberRepository memberRepository;
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//아이디 중복 검사
	public MemberSignUpRequestDto registerMember(MemberSignUpRequestDto dto) {
		//DB에 이미 입력한 아이디가 있는지 확인
		if(memberRepository.existsByMemberId(dto.getMemberId())) {
			//중복이면 예외발생시켜서 회원가입 막음
			throw new IllegalArgumentException("이미 존재하는 아이디 입니다.");
		}
		
		//Entity 변환
		MemberEntity newMember = MemberEntity.builder()
				.memberId(dto.getMemberId())
				.memberPw(dto.getMemberPw()) //비밀번호는 나중에 암호화 필요
				.memberName(dto.getMemberName())
				.memberBirth(dto.getMemberBirth())
				.memberPhone(dto.getMemberPhone())
				.memberAddress(dto.getMemberAddress())
				.memberSex(dto.getMemberSex())
		        .memberState(MemberState.ACTIVE) // 기본 상태
		        .memberLock(false)
		        .snsYn(dto.isSnsYn())
		        .kakaoId(dto.getKakaoId())
		        .build() //빌드
		        
	}
	
}
