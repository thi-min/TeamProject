package com.project.common.comtroller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.common.jwt.JwtTokenProvider;
import com.project.member.dto.MemberLoginRequestDto;
import com.project.member.dto.MemberLoginResponseDto;
import com.project.member.dto.MemberMyPageResponseDto;
import com.project.member.dto.MemberMyPageUpdateRequestDto;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.member.service.MemberService;

import lombok.RequiredArgsConstructor;

//인증 전용 컨트롤러
//JWT 토큰 발급

//로그인 요청처리
//인증된 사용자의 중복 로그인 방지
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	
	private final MemberService memberService;
	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;
	
	//로그인 엔드포인트
	//사용자가 로그인 요청을 보낼때 호출함
	//성공시 JWT토큰을 발급해서 로그인 응답에 포함시킴
	//이미 로그인된 사용자(토큰있음)는 로그인 차단
	//param : loginDto 사용자 로그인 요청 정보(아이디/비밀번호)
	//return : MemberLoginResponseDto + JWT 토큰 포함
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody MemberLoginRequestDto loginDto) {
		//현재 인증된 사용자인지 확인 여부
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    
	    if (authentication != null && authentication.isAuthenticated() &&
	        !(authentication instanceof AnonymousAuthenticationToken)) {
	    	// 이미 인증된 사용자라면 로그인 거부
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이미 로그인된 사용자입니다.");
	    }
	    //인증 시도 및 사용자 조회
	    MemberLoginResponseDto response = memberService.login(loginDto);
	    //JWT 토큰 발급
	    String accessToken = jwtTokenProvider.generateAccessToken(response.getMemberId());
	    String refreshToken = jwtTokenProvider.generateRefreshToken(response.getMemberId());
	    
	    response.setAccessToken(accessToken);	//응답 Dto에 토큰 추가
	    response.setRefreshToken(refreshToken);	//응답 Dto에 토큰 추가
	    
	    //RefreshToken DB 저장
	    MemberEntity member = memberRepository.findByMemberId(response.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
	    member.setRefreshToken(refreshToken);
	    memberRepository.save(member);
	    
	    //로그인 성공 응답
	    return ResponseEntity.ok(response);
	}
	
	//로그아웃 요청 처리
	//저장된 RefreshToken을 삭제하여 재발급 방지
	//클라이언트는 토큰 삭제
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String tokenHeader){
		if(tokenHeader == null || !tokenHeader.startsWith("bearer")) {
			return ResponseEntity.badRequest().body("잘못된 토큰 형식입니다.");
		}
		
		String token = tokenHeader.substring(7);
		
		if(!jwtTokenProvider.validateToken(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않는 토큰입니다.");
		}
		
		String memberId = jwtTokenProvider.getMemberIdFromToken(token);
		
		MemberEntity member = memberRepository.findByMemberId(memberId)
		           .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
		
		//refresh token 제거
		member.setRefreshToken(null);
		memberRepository.save(member);
		
		return ResponseEntity.ok("로그아웃 완료");
	}
	
	//인증된 마이페이지 조회
	//현재 로그인한 사용자의 마이페이지를 조회합니다.
	//인증정보에서 사용자의 ID를 추출해 memberNum기반으로 조회
	@GetMapping("/me")
	public ResponseEntity<MemberMyPageResponseDto> myPage(){
		//현재 인증 정보 가져오기
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		//인증이 안된경우
		if(auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		
		//인증된 사용자 ID 추출
		String memberId = auth.getName();	//principal로 전달된 memberId
		
		//사용자 정보 조회(memberNum 얻기 위함)
		MemberEntity member = memberRepository.findByMemberId(memberId)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
		
		//마이페이지 정보 반환
		return ResponseEntity.ok(memberService.myPage(member.getMemberNum()));
	}
	
	//인증된 마이페이지 수정(토큰으로 본인확인)
	//현재 로그인한 사용자의 마이페이지 정보를 수정합니다.
	//인증 정보를 기반으로 해당 사용자만 수정 가능하도록 합니다.
	@PutMapping("/me")
	public ResponseEntity<MemberMyPageResponseDto> updateMyPage(@RequestBody MemberMyPageUpdateRequestDto dto){
		//현재 인증 정보 가져오기
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		//인증이 안된경우
		if(auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		
		//인증된 사용자 ID 추출
		String memberId = auth.getName();	//principal로 전달된 memberId
		
		//사용자 정보 조회(memberNum 얻기 위함)
		MemberEntity member = memberRepository.findByMemberId(memberId)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
				
		//마이페이지 수정 로직 호출 및 결과 반환
		return ResponseEntity.ok(memberService.updateMyPage(member.getMemberNum(), dto));
	}
	
	//토큰재발급 추가
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(@RequestHeader("Authorization") String refreshTokenHeader){
		//1. bearer 헤더에서 토큰 추출
		if(refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 리프레시 토큰입니다.");
		}
		String refreshToken = refreshTokenHeader.substring(7);
		
		//2. refreshToken 유효성검증
		if(!jwtTokenProvider.validateToken(refreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않는 리프레시 토큰입니다.");
		}
		
		//3. 사용자 ID 추출
		String memberId = jwtTokenProvider.getMemberIdFromToken(refreshToken);
		
		//4. 사용자 존재 확인 + 토큰 일치 여부 확인
		MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
		if(!refreshToken.equals(member.getRefreshToken())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 불일치 - 재로그인 필요");
		}
		
		//5. 새로운 AccessToken 발급
		String newAccessToken = jwtTokenProvider.generateAccessToken(memberId);
		return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
	}
}
