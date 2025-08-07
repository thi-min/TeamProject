package com.project.member.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.common.jwt.JwtTokenProvider;
import com.project.member.dto.KakaoSignUpRequestDto;
import com.project.member.dto.KakaoUserInfoDto;
import com.project.member.dto.MemberLoginResponseDto;
import com.project.member.dto.MemberSignUpResponseDto;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.member.service.KakaoApiService;
import com.project.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Service
public class KakaoAuthController {
	//목적
//	프론트에서 리디렉션된 URL로 들어오는 인가 코드(code)를 받음
//	KakaoApiService를 통해 access token, 사용자 정보 받아옴
//	DB 조회 → 기존 회원이면 로그인 처리
//	신규면 회원가입 페이지 유도 + 사용자 정보 미리 전달
	
	//의존성주입
	private final KakaoApiService kakaoApiService;
	private final MemberService memberService;
	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;	//일반 로그인에서 사용중인 JWT 유틸
	
	@GetMapping("/oauth/kakao/test-user-info")
	public ResponseEntity<?> testKakaoUser(@RequestParam String accessToken) throws Exception {
	    KakaoUserInfoDto user = kakaoApiService.getUserInfo(accessToken);
	    return ResponseEntity.ok(user);
	}

	
	//카카오 로그인 콜백 처리
	@GetMapping("/kakao/callback")
	public ResponseEntity<MemberLoginResponseDto> kakaoLogin(@RequestParam String code) throws Exception{
		return ResponseEntity.ok(memberService.handleKakaoLogin(code));
//		
//		//카카오 access token 요청
//		String accessToken = kakaoApiService.getAccessToken(code);
//		//사용자 정보 요청 (kakaoId 포함)
//		KakaoUserInfoDto userInfo = kakaoApiService.getUserInfo(accessToken);
//		//기존 회원 여부 조회 (kakaoId로 memberId 대체)
//		Optional<MemberEntity> existing = memberRepository.findByKakaoId(userInfo.getKakaoId());
//		
//		if(existing.isPresent()) {
//			//기존 카카오회원 > 로그인처리 > JWT발급
//			MemberEntity member = existing.get();
//			String jwtAccess = jwtTokenProvider.generateAccessToken(member.getKakaoId());
//			String jwtRefresh = jwtTokenProvider.generateRefreshToken(member.getKakaoId());
//			return ResponseEntity.ok(MemberLoginResponseDto.builder()
//					.memberId(member.getMemberId())
//					.memberName(member.getMemberName())
//					.accessToken(jwtAccess)
//					.refreshToken(jwtRefresh)
//					.requireSignup(false)
//					.build());
//		}else{
//			//신규회원 : 회원가입 + 카카오정보 제공
//			String birth = parseBirth(userInfo.getBirthyear(), userInfo.getBirthday());
//			String phone = formatPhoneNumber(userInfo.getPhoneNumber());
//			
//			return ResponseEntity.ok(MemberLoginResponseDto.builder()
//					.memberId(userInfo.getKakaoId()) // memberId로 사용
//                    .kakaoId(userInfo.getKakaoId())
//                    .memberName(userInfo.getNickname())
//                    .gender(userInfo.getGender())
//                    .birth(birth)	//yyyy-mm-dd
//                    .phone(phone)	//010-0000-0000
//                    .requireSignup(true)
//                    .build());
//		}
	}
	
	//카카오 로그인 신규 회원가입 처리
	@PostMapping("/signup/kakao")
	public ResponseEntity<MemberSignUpResponseDto> kakaoSignup(@RequestBody KakaoSignUpRequestDto dto){
		MemberEntity saved = memberService.kakaoSignUp(dto);
		return ResponseEntity.ok(new MemberSignUpResponseDto(
				saved.getMemberNum(),
				saved.getMemberId(),
				"카카오 회원가입 성공"
				));
	}
		
}
