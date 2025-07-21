package com.project.phoneVeify.service;

import org.springframework.stereotype.Service;
import com.project.member.entity.MemberEntity;
import lombok.RequiredArgsConstructor;

@Service //tjqltmrPcmd(spring bean)으로 등록
@RequiredArgsConstructor //final로 선언된 memberRepository를 자동으로 생성자 주입 시켜줌
public class PhoneVeifyServiceImpl {

	private PhoneVeifyRepository phoneVeifyRepository;
	
	//휴대폰번호로 회원여부 확인
	public String findMemberPhone(String phoneNum, String memberNum) {
		MemberEntity member = phoneVeifyRepository.findByMemberCheck(phoneNum, memberNum)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
		
		return "조회가 완료되었습니다.";
	}
	
	//인증번호 생성 후 문자 발송 로직
	public void certificationNumer(String phoneNumber) {
		String code = certificationCode(); //6자리 숫자 인증번호 생성
		
		//기존에 인증기록 있으면 덮어씌우기 or update
		PhoneAuth phoneAuth = new PhoneAuth(phoneNumber, code);
		phoneAuthRepository.save(phoneAuth);
		//실제 sns 발송 서비스 호출 (미구현)
		//예) naver, coolsms, twilio
		smsService.send(phoneNumber, "[함께마당] 인증번호는 ["+code+"] 입니다.");
	}
	
	//사용자가 입력한 인증번호 검증 로직
	public boolean verifyCode(String phoneNumber, String inputCode) {
		//인증번호 조회
		PhoneAuth auth = phoneVeifyRepository.findByPhoneNumber(phoneNumber)
				.orElseThrow(IllegalArgumentException("인증 요청내역이 없습니다."));
		
		//인증번호 비교
		private String certificationCode() {
			//100000 ~ 999999까 범위의 랜덤 함수 생성
			return String.valueOf((ine)(Math.random() * 900000) + 100000);
		}
	}
}
