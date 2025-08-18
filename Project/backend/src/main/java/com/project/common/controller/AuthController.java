package com.project.common.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
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
	private final AdminRepository adminRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;
	
	// ✅ 공용 로그인: 관리자 이메일이면 role=ADMIN, 아니면 role=USER
	// - 응답 JSON: { "member": MemberLoginResponseDto, "isPasswordExpired": boolean }
	// - 프론트는 res.data.member.role로 바로 분기 가능
	
	@Value("${app.admin.id:admin@admin.kr}")
	private String adminEmailConfig;
	
	 @PostMapping("/login")
	    public ResponseEntity<?> login(@RequestBody MemberLoginRequestDto loginDto) {
	        // 이미 로그인된 사용자인지 확인(선택)
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        if (authentication != null && authentication.isAuthenticated()
	                && !(authentication instanceof AnonymousAuthenticationToken)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이미 로그인된 사용자입니다.");
	        }

	        final String loginId = loginDto.getMemberId();
	        final boolean isAdminLogin = loginId != null && loginId.equalsIgnoreCase(adminEmailConfig);

	        MemberLoginResponseDto response;

	        if (isAdminLogin) {
	            // =========================
	            // ✅ 관리자 인증 경로
	            // =========================
	            AdminEntity admin = adminRepository.findFirstByAdminId(loginId)
	                    .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

	            if (!passwordEncoder.matches(loginDto.getMemberPw(), admin.getAdminPw())) {
	                throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
	            }

	            response = MemberLoginResponseDto.builder()
	            		.memberNum(admin.getAdminNum())
	                    .memberId(admin.getAdminId())   // 이메일=ID
	                    .memberName(admin.getAdminName())
	                    .message("로그인 성공")
	                    .role("ADMIN")                  // ✅ DTO에 role 채움
	                    .build();

	            // 토큰 발급(ADMIN)
	            String accessToken  = jwtTokenProvider.generateAccessToken(response.getMemberId(), "ADMIN");
	            String refreshToken = jwtTokenProvider.generateRefreshToken(response.getMemberId());
	            response.setAccessToken(accessToken);
	            response.setRefreshToken(refreshToken);

	            // 관리자 토큰 저장/접속시간 갱신
	            admin.setAccessToken(accessToken);
	            admin.setRefreshToken(refreshToken);
	            admin.setConnectData(LocalDateTime.now());
	            adminRepository.save(admin);

	            // 관리자 비밀번호 만료 체크는 기본 false 처리(정책에 따라 구현)
	            return ResponseEntity.ok(Map.of(
	                    "member", response,
	                    "isPasswordExpired", false
	            ));
	        }

	        // =========================
	        // ✅ 일반 회원 인증 경로 (기존 로직)
	        // =========================
	        MemberLoginResponseDto userRes = memberService.login(loginDto);

	        // 토큰 발급(USER)
	        String accessToken  = jwtTokenProvider.generateAccessToken(userRes.getMemberId(), "USER");
	        String refreshToken = jwtTokenProvider.generateRefreshToken(userRes.getMemberId());
	        userRes.setAccessToken(accessToken);
	        userRes.setRefreshToken(refreshToken);
	        userRes.setRole("USER"); // ✅ DTO에 role 채움
	        
	        // DB 저장(Refresh/Access)
	        MemberEntity member = memberRepository.findByMemberId(userRes.getMemberId())
	                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
	        member.setAccessToken(accessToken);
	        member.setRefreshToken(refreshToken);
	        if (member.getPwUpdated() == null) member.setPwUpdated(LocalDateTime.now());
	        memberRepository.save(member);

	        userRes.setMemberNum(member.getMemberNum());	//강민씨 추가
	        
	        boolean isExpired = memberService.isPasswordExpired(member);

	        return ResponseEntity.ok(Map.of(
	                "member", userRes,
	                "isPasswordExpired", isExpired
	        ));
	    }
	 
//	@PostMapping("/login")
//	public ResponseEntity<?> login(@RequestBody MemberLoginRequestDto loginDto) {
//	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//	    if (authentication != null && authentication.isAuthenticated() &&
//	        !(authentication instanceof AnonymousAuthenticationToken)) {
//	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이미 로그인된 사용자입니다.");
//	    }
//	
//	    // 1) 사용자 인증 (아이디/비번 검증) → 기존 로직 유지
//	    MemberLoginResponseDto response = memberService.login(loginDto);
//	
//	    // 2) 관리자 여부 판정: 설정된 관리자 이메일과 일치하면 ADMIN, 아니면 USER
//	    final boolean isAdmin = response.getMemberId().equalsIgnoreCase(adminEmailConfig);
//	    final String role = isAdmin ? "ADMIN" : "USER";
//	
//	    // 3) 토큰 발급 (role 포함 버전 사용 권장)
//	    //jwtTokenProvider.generateAccessToken(String subject, String role) 형태가 없다면 오버로드 추가 필요
//	    String accessToken = jwtTokenProvider.generateAccessToken(response.getMemberId(), role);
//	    String refreshToken = jwtTokenProvider.generateRefreshToken(response.getMemberId());
//
//	    // 4) 응답 DTO에 토큰 + 역할 세팅
//	    response.setAccessToken(accessToken);
//	    response.setRefreshToken(refreshToken);
//	    response.setRole(role); // ✅ 여기서 세팅 → 프론트가 response.member.role 사용
//
//	
//	    // 5) Refresh 토큰 저장 및 기타 업데이트(기존 로직)
//	    MemberEntity member = memberRepository.findByMemberId(response.getMemberId())
//	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
//	    member.setAccessToken(accessToken);
//	    member.setRefreshToken(refreshToken);
//	    if (member.getPwUpdated() == null) {
//	        member.setPwUpdated(LocalDateTime.now());
//	    }
//	    memberRepository.save(member);
//	    
//	    // 6) 비밀번호 만료 체크
//	    boolean isPasswordExpired = memberService.isPasswordExpired(member);
//
//	    // 7) 최종 응답: role은 member DTO 안에 포함돼 있으므로 Map에 따로 넣을 필요 없음
//	    return ResponseEntity.ok(Map.of(
//	        "member", response,
//	        "isPasswordExpired", isPasswordExpired
//	    ));
//	}

	// ✅ 공용 로그아웃 (회원/관리자 공통)
	// - Authorization: Bearer <accessToken> 헤더 필수
	// - 1) 토큰 형식 점검 → 2) 토큰 유효성 검사 → 3) subject(memberId) 추출
	// - 4) DB의 RefreshToken(및 저장해둔 AccessToken) 제거 → 5) 200 OK
	// - 주의: JWT는 stateless라 AccessToken은 서버상 즉시 "무효화"가 불가.
//	         운영 시에는 AccessToken 블랙리스트(예: Redis)로 보조 무효화를 권장.

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String tokenHeader) {

        // 0) 헤더 존재/형식 확인 (대소문자 안전)
        if (tokenHeader == null || !tokenHeader.toLowerCase().startsWith("bearer ")) {
            return ResponseEntity.badRequest().body("잘못된 토큰 형식입니다.");
        }

        // 1) "Bearer " 제거 + 공백 정리
        final String token = tokenHeader.substring(7).trim();

        // 2) 토큰 유효성 검사 (서명/만료 등)
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않는 토큰입니다.");
        }

        // 3) subject 추출 (회원/관리자 공통 식별자: memberId/email)
        final String memberId = jwtTokenProvider.getMemberIdFromToken(token);

        // 4) DB에서 회원 조회 → RefreshToken 제거 (+ 저장된 AccessToken도 유지 이유 없으면 제거)
        MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        member.setRefreshToken(null); // ✅ 재발급 차단(서버 보유 토큰 제거)
        member.setAccessToken(null);  // (선택) 보관하던 액세스 토큰도 정리
        memberRepository.save(member);

        return ResponseEntity.ok("로그아웃 완료");
    }

	//로그아웃 요청 처리
	//저장된 RefreshToken을 삭제하여 재발급 방지
	//클라이언트는 토큰 삭제
//	@PostMapping("/logout")
//	public ResponseEntity<?> logout(@RequestHeader("Authorization") String tokenHeader){
//		if (tokenHeader == null || !tokenHeader.toLowerCase().startsWith("bearer ")) {
//		    return ResponseEntity.badRequest().body("잘못된 토큰 형식입니다.");
//		}
//		
//		String token = tokenHeader.substring(7);
//		
//		if(!jwtTokenProvider.validateToken(token)) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않는 토큰입니다.");
//		}
//		
//		String memberId = jwtTokenProvider.getMemberIdFromToken(token);
//		
//		MemberEntity member = memberRepository.findByMemberId(memberId)
//		           .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
//		
//		//refresh token 제거
//		member.setRefreshToken(null);
//		memberRepository.save(member);
//		
//		return ResponseEntity.ok("로그아웃 완료");
//	}
	
	//인증된 마이페이지 조회
	//현재 로그인한 사용자의 마이페이지를 조회합니다.
	//인증정보에서 사용자의 ID를 추출해 memberNum기반으로 조회
	@GetMapping("/mypage")
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
		
	    if(memberService.isPasswordExpired(member)) {
	        throw new IllegalStateException("비밀번호가 만료되어 마이페이지 접근이 제한됩니다.");
	    }
	    
		//마이페이지 정보 반환
		return ResponseEntity.ok(memberService.myPage(member.getMemberNum()));
	}
	
	//인증된 마이페이지 수정(토큰으로 본인확인)
	//현재 로그인한 사용자의 마이페이지 정보를 수정합니다.
	//인증 정보를 기반으로 해당 사용자만 수정 가능하도록 합니다.
	@PutMapping("/mypage")
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
