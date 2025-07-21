package com.project.phoneVeify.service;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.phoneVeify.dto.PhoneAuthRequestDto;
import com.project.phoneVeify.dto.PhoneAuthVerifyDto;
import com.project.phoneVeify.entity.PhoneAuthEntity;
import com.project.phoneVeify.repository.PhoneVeifyRepository;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Service //tjqltmrPcmd(spring bean)으로 등록
@RequiredArgsConstructor //final로 선언된 memberRepository를 자동으로 생성자 주입 시켜줌
public class PhoneVeifyServiceImpl implements PhoneVeifyService{

    private final MemberRepository memberRepository;
	private final PhoneVeifyRepository phoneVeifyRepository;
	
	//휴대폰번호로 회원여부 확인
//	public String checkPhone(String phoneNum) {
//		if(memberRepository.existsByMemberPhone(phoneNum)) {
//			throw new IllegalArgumentException("이미 가입된 회원입니다.");
//		}
//		return "회원가입 가능합니다.";
//	}
	
	//인증번호 생성 후 문자 발송 로직
	public void certificationNumber(PhoneAuthRequestDto dto) {
		String code = sendNumer(); //6자리 숫자 인증번호 생성
		
		//기존에 인증기록 있으면 덮어씌우기 or update
		PhoneAuthEntity phoneAuth = PhoneAuthEntity.builder()
				.phoneNum(dto.getPhoneNum()) //입력된 사용자 핸드폰 번호
				.authCode(code)		//위에서 생성한 6자리 인증번호(code)
				.verified(false)	//인증 여부 초기값(false)
				.requestTime(LocalDateTime.now())	//인증 요청 시간
				.build();
		
		phoneVeifyRepository.save(phoneAuth);
		//실제 sns 발송 서비스 호출 (미구현)
		//예) naver, coolsms, twilio
		System.out.println("[인증번호 전송] " + dto.getPhoneNum() + " → " + code);
	}
	
	//사용자가 입력한 인증번호 검증 로직
	public boolean verifyCode(PhoneAuthVerifyDto dto) {
		//인증번호 조회
		PhoneAuthEntity phoneAuth = phoneVeifyRepository.findByPhoneNumber(dto.getPhoneNum())
				.orElseThrow(() -> new IllegalArgumentException("먼저 휴대폰인증 해주세요."));
		
		//인증번호가 동일한지 체크
		if(!phoneAuth.getAuthCode().equals(dto.getAuthNum())) {
			throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
		}
		
		//인증성공시 상태 업데이트
		phoneAuth.setVerified(true);
		//인증 성공시 변경된 상태를 DB에 반영
		phoneVeifyRepository.save(phoneAuth);
		
		return true;
		
	}
	//인증번호 비교
	private String sendNumer() {
		//100000 ~ 999999까 범위의 랜덤 함수 생성
		return String.valueOf((int)(Math.random() * 900000) + 100000);
	}

}
