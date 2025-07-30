package com.project.member.service;

//비밀번호 단뱡향 복호화
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;

import com.project.common.util.JasyptUtil;
import com.project.member.dto.MemberDeleteDto;
import com.project.member.dto.MemberLoginRequestDto;
import com.project.member.dto.MemberLoginResponseDto;
import com.project.member.dto.MemberMyPageResponseDto;
import com.project.member.dto.MemberMyPageUpdateRequestDto;
import com.project.member.dto.MemberPasswordUpdateRequestDto;
import com.project.member.dto.MemberSignUpRequestDto;
import com.project.member.dto.MemberSignUpResponseDto;
import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberState;
import com.project.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service //tjqltmrPcmd(spring bean)으로 등록
@RequiredArgsConstructor //final로 선언된 memberRepository를 자동으로 생성자 주입 시켜줌
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
	//회원가입
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//아이디 중복 검사
	public MemberSignUpResponseDto sigup(MemberSignUpRequestDto dto) {
		//DB에 이미 입력한 아이디가 있는지 확인
		if(memberRepository.existsByMemberId(dto.getMemberId())) {
			//중복이면 예외발생시켜서 회원가입 막음
			throw new IllegalArgumentException("이미 존재하는 아이디 입니다.");
		}
		
		//비밀번호 암호화
		String encodedPw = passwordEncoder.encode(dto.getMemberPw());
		//핸드폰번호 암호화
		String encryptedPhone = JasyptUtil.encrypt(dto.getMemberPhone());
		
		//Entity 변환
		MemberEntity newMember = MemberEntity.builder()
				.memberId(dto.getMemberId())
				.memberPw(encodedPw)
				.memberName(dto.getMemberName())
				.memberBirth(dto.getMemberBirth())
				.memberPhone(encryptedPhone)
				.memberAddress(dto.getMemberAddress())
				.memberSex(dto.getMemberSex())
		        .memberState(MemberState.ACTIVE) // 기본 상태
		        .memberLock(false)
		        .smsAgree(dto.isSmsAgree())
		        .kakaoId(dto.getKakaoId())
		        .build();
		//DB저장
		MemberEntity saved = memberRepository.save(newMember);
		
		//응답 DTO 반환
		return new MemberSignUpResponseDto(saved.getMemberId(), "회원가입 완료");
	}

	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//로그인
	public MemberLoginResponseDto login(MemberLoginRequestDto dto) {
		//아이디와 비밀번호로 회원 정보 조회
		MemberEntity member = memberRepository
			.findByMemberIdAndMemberPw(dto.getMemberId(), dto.getMemberPw())
			.orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
		
		//로그인 성공 시 필요한 정보 dto 반환
		return MemberLoginResponseDto.builder()
				.memberId(member.getMemberId())
				.memberName(member.getMemberName())
				.message("로그인 성공")
				.accessToken("정상 토큰")
				.refreshToken("재발급 토큰")
				.build();
	}
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//마이페이지
	public MemberMyPageResponseDto myPage(Long memberNum) {
		MemberEntity member = memberRepository.findByMemberNum(memberNum)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
		
		//핸드폰번호 복호화
		String decryptedPhone = JasyptUtil.decrypt(member.getMemberPhone());
		
		return MemberMyPageResponseDto.builder()
				.memberName(member.getMemberName())
				.memberId(member.getMemberId())
				.memberBirth(member.getMemberBirth())
				.memberSex(member.getMemberSex()) //enum은 그대로 호출
				.memberAddress(member.getMemberAddress())
				.memberPhone(decryptedPhone)
				.kakaoId(member.getKakaoId())
				.smsAgree(member.isSmsAgree()) //boolean타입은 is로 호출
				.build();
	}
	
	//마이페이지 수정 + sms 동의
	@Transactional
	@Override
	public MemberMyPageResponseDto updateMyPage(Long memberNum, MemberMyPageUpdateRequestDto dto) {
	    MemberEntity member = memberRepository.findByMemberNum(memberNum)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

	    // 수정 가능한 항목만 반영
	    member.setMemberName(dto.getMemberName());
	    member.setMemberSex(dto.getMemberSex());
	    member.setMemberPhone(dto.getMemberPhone());
	    member.setMemberAddress(dto.getMemberAddress());
	    member.setSmsAgree(dto.isSmsAgree()); //체크박스 상태 반영

	    return MemberMyPageResponseDto.builder()
	            .memberName(member.getMemberName())
	            .memberId(member.getMemberId())
	            .memberBirth(member.getMemberBirth())
	            .memberSex(member.getMemberSex())
	            .memberAddress(member.getMemberAddress())
	            .memberPhone(member.getMemberPhone())
	            .kakaoId(member.getKakaoId())
	            .smsAgree(member.isSmsAgree())
	            .build();
	}

	
//	//회원 sns 동의
//	@Transactional
//	@Override
//	public MemberSmsAgreeUpdateResponseDto updateSmsAgree(Long memberNum, MemberSmsAgreeUpdateRequestDto dto) {
//		MemberEntity member = memberRepository.findByMemberNum(memberNum)
//				.orElseThrow(() -> new IllegalArgumentException("회원 계정에 문제가 발생했습니다."));
//		
//		member.setSmsAgree(dto.isSmsAgree());
//		
//		String message = dto.isSmsAgree() ? "SMS 수신 동의가 설정되었습니다." : "SMS 수신 동의가 해제 되었습니다.";
//		
//		return new MemberSmsAgreeUpdateResponseDto(member.getMemberNum(), member.isSmsAgree(), message);
//	}
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//회원탈퇴
	public MemberDeleteDto memberOut(Long memberNum) {
		MemberEntity member = memberRepository.findByMemberNum(memberNum)
				.orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
		
		memberRepository.delete(member);
		
		return new MemberDeleteDto(member.getMemberNum(), member.getMemberName(), "회원 탈퇴 완료");
	}
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//아이디 찾기
	public String findMemberId(String memberName, String memberPhone) {
		MemberEntity member = memberRepository.findByMemberNameAndMemberPhone(memberName, memberPhone)
				.orElseThrow(() -> new IllegalArgumentException("일치하는 아이디가 없습니다."));
		
		return member.getMemberId(); //마스킹 처리해서 반환해도됨
	}
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//비밀번호 찾기
	public String findMemberPw(String memberId, String memberName, String memberPhone) {
		MemberEntity member = memberRepository.findByMemberIdAndMemberNameAndMemberPhone(memberId, memberName, memberPhone)
				.orElseThrow(() -> new IllegalArgumentException("입력하신 정보와 일치하는 회원이 없습니다."));
		
		return "본인 확인이 완료되었습니다. 비밀번호를 재설정 해주세요";
	}
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//비밀번호 변경
	public void updatePassword(MemberPasswordUpdateRequestDto dto) {
	    String memberId = dto.getMemberId(); // 여기서 꺼냄
	    MemberEntity member = memberRepository.findByMemberId(memberId)
	        .orElseThrow(() -> new IllegalArgumentException("회원 없음"));
	    
	    //비밀번호 단뱡향 복호화
		//현재 비밀번호 검증
		if(!passwordEncoder.matches(dto.getCurrentPassword(), member.getMemberPw())) {
			throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
		}
		//새 비밀번호와 비밀번호 확인 일치 여부
		if(!dto.getNewPassword().equals(dto.getNewPasswordCheck())) {
			throw new IllegalArgumentException("변경할 비밀번호가 일치하지 않습니다.");
		}
		//이전 비밀번호와 같은지 확인
		if(passwordEncoder.matches(dto.getNewPassword(), member.getMemberPw())) {
			throw new IllegalArgumentException("이전과 동일한 비밀번호는 사용할 수 없습니다.");
		}
		
		//새 비밀번호 암호화 및 저장
		String newEncodePw = passwordEncoder.encode(dto.getNewPassword());
		member.setMemberPw(newEncodePw);
		memberRepository.save(member); //저장
	}

	//휴대폰 번호로 회원 존재 여부 확인
	public String checkPhoneNumber(String phoneNum) {
	    String encryptedPhone;
		//memberPhone컬럼에 phoneNum와 같은 값이 존재하는지 조회
	    try {
	    	//입력값을 암호화
	        encryptedPhone = JasyptUtil.encrypt(phoneNum);
	        System.out.println("📦 암호화된 입력값: " + encryptedPhone); // 🔍 여기에 로그 찍기
	    } catch (Exception e) {
	        throw new RuntimeException("휴대폰 번호 확인중 암호화 오류 발생", e);
	    }
	    //암호화된 값으로 조회
	    boolean exists = memberRepository.findByMemberPhone(encryptedPhone).isPresent();

	    //동일한 값이 존재한다면 예외 발생
	    if (exists) {
	        throw new IllegalArgumentException("이미 가입된 휴대폰 번호입니다.");
	    }
	    //존재하지 않으면 인증가능
	    return "사용 가능한 번호입니다.";
	    
		//1. 사용자가 핸드폰번호 입력
  		//2. encrypt 핸드폰번호 암호화
  		//3. 암호화된 문자열을 memberPhone과 비교
  		//4. 존재여부 판단 > 중복 확인 처리
	}
}
