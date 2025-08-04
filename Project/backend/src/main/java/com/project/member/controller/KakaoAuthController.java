package com.project.member.controller;

import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.member.service.KakaoApiServiceImpl;
import com.project.member.entity.MemberState;
import com.project.member.dto.KakaoUserInfoDto;
import com.project.member.dto.MemberLoginResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class KakaoAuthController {
	//목적
//	프론트에서 리디렉션된 URL로 들어오는 인가 코드(code)를 받음
//	KakaoApiService를 통해 access token, 사용자 정보 받아옴
//	DB 조회 → 기존 회원이면 로그인 처리
//	신규면 회원가입 페이지 유도 + 사용자 정보 미리 전달
	
	//의존성주입
	private final KakaoApiServiceImpl kakaoApiService;
	private final MemberRepository memberRepository;
	
	@GetMapping("/kakao/callback")
	public MemberLoginResponseDto kakaoLogin(@RequestParam String code) throws Exception{
		//카카오 access token 요청
		String accessToken = kakaoApiService.getAccessToken(code);
		//사용자 정보 요청 (kakaoId 포함)
		KakaoUserInfoDto userInfo = kakaoApiService.getUserInfo(accessToken);
		//기존 회원 여부 조회 (kakaoId로 memberId 대체)
		Optional<MemberEntity> existing = memberRepository.findByKakaoId(userInfo.getKakaoId());
		
		if(existing.isPresent()) {
			//기존 회원 로그인 처리
			MemberEntity member = existing.get();
			return MemberLoginResponseDto.builder()
					.memberId(member.getMemberId())
					.memberName(member.getMemberName())
					.requireSignup(false)
					.build();
		}else{
			//신규회원 : 회원가입 + 카카오정보 제공
			return MemberLoginResponseDto.builder()
					.memberId(userInfo.getKakaoId()) // memberId로 사용
                    .kakaoId(userInfo.getKakaoId())
                    .email(userInfo.getEmail())
                    .memberName(userInfo.getNickname())
                    .gender(userInfo.getGender())
                    .birth(userInfo.getBirthyear() + "-" + userInfo.getBirthday())
                    .phone(userInfo.getPhoneNumber())
                    .requireSignup(true)
                    .build();
		}
	}
}
