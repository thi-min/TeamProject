package com.project.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.member.dto.MemberDetailResponseDto;
import com.project.member.dto.MemberForcedDeleteDto;
import com.project.member.dto.MemberListResponseDto;
import com.project.admin.dto.AdminMemberDetailResponseDto;
import com.project.admin.dto.LoginRequestDto;
import com.project.admin.repository.AdminRepository;
import com.project.member.dto.MemberLoginResponseDto;
import com.project.member.dto.MemberMyPageResponseDto;
import com.project.member.dto.MemberPasswordUpdateRequestDto;
import com.project.member.dto.MemberSignUpRequestDto;
import com.project.member.dto.MemberSignUpResponseDto;
import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberState;
import com.project.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service //tjqltmrPcmd(spring bean)으로 등록
@Transactional
@RequiredArgsConstructor //final로 선언된 memberRepository를 자동으로 생성자 주입 시켜줌
public class AdminServiceImpl implements AdminService {

	private final AdminRepository adminRepository;
	private final MemberRepository memberRepository;

	@Override
	//관리자 로그인
	public MemberLoginResponseDto login(LoginRequestDto dto) {
		//아이디와 비밀번호로 회원 정보 조회
		MemberEntity member = memberRepository
			.findByMemberIdAndMemberPw(dto.getMemberId(), dto.getMemberPw())
			.orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
		
		//로그인 성공 시 필요한 정보 dto 반환
		return MemberLoginResponseDto.builder()
				.memberId(member.getMemberId())
				.memberName(member.getMemberName())
				.message("로그인 성공")
				.accessToken("생성된 토큰값")
				.build();
	}
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//마이페이지
	public MemberMyPageResponseDto myPage(Long memberNum) {
		MemberEntity member = memberRepository.findByMemberNum(memberNum)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
		
		return MemberMyPageResponseDto.builder()
				.memberName(member.getMemberName())
				.memberId(member.getMemberId())
				.memberBirth(member.getMemberBirth())
				.memberSex(member.getMemberSex()) //enum은 그대로 호출
				.memberAddress(member.getMemberAddress())
				.memberPhone(member.getMemberPhone())
				.kakaoId(member.getKakaoId())
				.snsYn(member.isSnsYn()) //boolean타입은 is로 호출
				.build();
	}
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//아이디 찾기
	public String findMemberId(String memberName, String memberPhone) {
		MemberEntity member = memberRepository.findByID(memberName, memberPhone)
				.orElseThrow(() -> new IllegalArgumentException("일치하는 아이디가 없습니다."));
		
		return member.getMemberId(); //마스킹 처리해서 반환해도됨
	}
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//비밀번호 찾기
	public String findMemberPw(String memberId, String memberName, String memberPhone) {
		MemberEntity member = memberRepository.findByPassword(memberId, memberName, memberPhone)
				.orElseThrow(() -> new IllegalArgumentException("입력하신 정보와 일치하는 회원이 없습니다."));
		
		return "본인 확인이 완료되었습니다. 비밀번호를 재설정 해주세요";
	}
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//비밀번호 변경
	public void updatePw(MemberPasswordUpdateRequestDto dto) {
		//아이디로 존재하는 회원인지 체크
		MemberEntity member = memberRepository.findByMemberNum(dto.getMemberNum())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
		
		//현재 비밀번호 검증
		if(!member.getMemberPw().equals(dto.getCurrentPassword())) {
			throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
		}
		//새 비밀번호와 비밀번호 확인 일치 여부
		if(!dto.getNewPassword().equals(dto.getCurrentPassword())) {
			throw new IllegalArgumentException("변경할 비밀번호가 일치하지 않습니다.");
		}
		
		//비밀번호 변경
		member.setMemberPw(dto.getNewPassword());
		
		//변경된 값 저장하기
		memberRepository.save(member); //저장
	}

	//휴대폰 번호로 회원 존재 여부 확인
	public String checkPhoneNumber(String phoneNum) {
		//memberPhone컬럼에 phoneNum와 같은 값이 존재하는지 조회
		boolean exists = memberRepository.findByMemberPhone(phoneNum).isPresent();
		
		//동일한 값이 존재한다면 예외 발생
		if(exists) {
			throw new IllegalArgumentException("이미 가입된 휴대폰 번호입니다.");
		}
		//존재하지 않으면 인증가능
		return "사용 가능한 번호입니다.";
	}
	
	//로그인(관리자)
	@Override
	public 
	
	//회원목록 조회(관리자)
	@Override
    //여러명의 회원(각각 기본키 보유)을 한번에 조회하기 떄문에 List사용
	public List<MemberListResponseDto> MemberList(){
		//MemberEntity 리스트를 조회하여, 프론트에 필요한 데이터만 MemberListResponseDto 형태로 변환 후 반환		
		return memberRepository.findAll().stream()
				//각 회원 데이터를 MemberListReponseDto 객체로 반환시킴
				.map(member -> MemberListResponseDto.builder()
						.memberNum(member.getMemberNum()) //회원 번호
						.memberId(member.getMemberId()) //회원 아이디(이메일)
						.memberName(member.getMemberName()) //회원이름
						//가입일(LocalDate -> 문자열로 변환, null 체크 여부확인
						.memberDay(member.getMemberDay() != null
								? member.getMemberDay().toString()
								: null)
						.memberState(member.getMemberState().name()) //회원상태
						.memberLock(Boolean.TRUE.equals(member.getMemberLock())) //계정 잠금여부
						.build() //dto객체 생성
					)
				//변환된 dto를 리스트로 수집
				.collect(Collectors.toList());
				
	}
	
	//회원정보 상세보기(관리자)
	@Override
	public AdminMemberDetailResponseDto AdminMemberDetailView(Long memberNum) {
		//조회할 memberNum 기준으로 MemberEntity 조회 //일치하는 회원이 없을경우 메시지 출력
		MemberEntity member = memberRepository.findByMemberNum(memberNum)
				.orElseThrow(() ->  new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
		
		return AdminMemberDetailResponseDto.builder()
				.memberNum(member.getMemberNum())		//회원번호
				.memberId(member.getMemberId())			//회원 아이디(이메일)
				.memberName(member.getMemberName())		//회원 이름
				.memberBirth(member.getMemberBirth())	//생년월일
				.memberPhone(member.getMemberPhone())	//핸드폰 번호
				.memberAddress(member.getMemberAddress())	//주소
				.memberSex(member.getMemberSex())		//성별
				.memberLock(Boolean.TRUE.equals(member.getMemberLock())) //계정 잠금상태
				.snsYn(member.isSnsYn())	//sms수신여부
				.build();
				
	}
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//회원탈퇴(관리자기준)
	public MemberForcedDeleteDto memberOut(Long memberNum) {
		MemberEntity member = memberRepository.findByMemberNum(memberNum)
				.orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
		
		memberRepository.delete(member);
		
		return new MemberForcedDeleteDto(member.getMemberNum(), "회원 탈퇴 완료");
	}

}
