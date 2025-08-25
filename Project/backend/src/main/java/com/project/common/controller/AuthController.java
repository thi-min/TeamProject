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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
import com.project.common.jwt.JwtTokenProvider;
import com.project.member.dto.MemberAuthResult;
import com.project.member.dto.MemberLoginRequestDto;
import com.project.member.dto.MemberLoginResponseDto;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.member.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

        // 이미 로그인 여부(선택)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이미 로그인된 사용자입니다.");
        }

        final String loginId = loginDto.getMemberId();
        final boolean isAdminLogin = loginId != null && loginId.equalsIgnoreCase(adminEmailConfig);

        // =========================
        // ✅ 관리자 인증 경로
        // =========================
        if (isAdminLogin) {
            AdminEntity admin = adminRepository.findFirstByAdminId(loginId)
                    .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

            if (!passwordEncoder.matches(loginDto.getMemberPw(), admin.getAdminPw())) {
                throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
            }

            // 토큰 발급(ADMIN)
            String accessToken  = jwtTokenProvider.generateAccessToken(admin.getAdminId(), "ADMIN");
            String refreshToken = jwtTokenProvider.generateRefreshToken(admin.getAdminId());

            // 저장
            admin.setAccessToken(accessToken);
            admin.setRefreshToken(refreshToken);
            admin.setConnectData(LocalDateTime.now());
            adminRepository.save(admin);

            MemberLoginResponseDto response = MemberLoginResponseDto.builder()
                    .memberNum(admin.getAdminNum())
                    .memberId(admin.getAdminId())
                    .memberName(admin.getAdminName())
                    .message("로그인 성공")
                    .role("ADMIN")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            return ResponseEntity.ok(Map.of(
                    "member", response,
                    "isPasswordExpired", false
            ));
        }

        // =========================
        // ✅ 일반 회원 인증 경로
        //  - Service.authenticate()로 상태/비번 검증만
        //  - 여기서 토큰 발급/저장
        // =========================
        MemberAuthResult auth = memberService.authenticate(loginDto);

        String accessToken  = jwtTokenProvider.generateAccessToken(auth.getMemberId(), "USER");
        String refreshToken = jwtTokenProvider.generateRefreshToken(auth.getMemberId());

        // DB 저장(Refresh/Access)
        MemberEntity member = memberRepository.findByMemberId(auth.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        member.setAccessToken(accessToken);
        member.setRefreshToken(refreshToken);
        if (member.getPwUpdated() == null) member.setPwUpdated(LocalDateTime.now());
        memberRepository.save(member);

        MemberLoginResponseDto userRes = MemberLoginResponseDto.builder()
                .memberNum(member.getMemberNum())
                .memberId(auth.getMemberId())
                .memberName(auth.getMemberName())
                .message("로그인 성공")
                .role("USER")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        boolean isExpired = memberService.isPasswordExpired(member);

        return ResponseEntity.ok(Map.of(
                "member", userRes,
                "isPasswordExpired", isExpired
        ));
    }
	
//	@PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody MemberLoginRequestDto loginDto) {
//        // 이미 로그인된 사용자인지 확인(선택)
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()
//                && !(authentication instanceof AnonymousAuthenticationToken)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이미 로그인된 사용자입니다.");
//        }
//
//        final String loginId = loginDto.getMemberId();
//        final boolean isAdminLogin = loginId != null && loginId.equalsIgnoreCase(adminEmailConfig);
//
//        MemberLoginResponseDto response;
//
//        if (isAdminLogin) {
//            // =========================
//            // ✅ 관리자 인증 경로
//            // =========================
//            AdminEntity admin = adminRepository.findFirstByAdminId(loginId)
//                    .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
//
//            if (!passwordEncoder.matches(loginDto.getMemberPw(), admin.getAdminPw())) {
//                throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
//            }
//
//            response = MemberLoginResponseDto.builder()
//            		.memberNum(admin.getAdminNum())
//                    .memberId(admin.getAdminId())   // 이메일=ID
//                    .memberName(admin.getAdminName())
//                    .message("로그인 성공")
//                    .role("ADMIN")                  // ✅ DTO에 role 채움
//                    .build();
//
//            // 토큰 발급(ADMIN)
//            String accessToken  = jwtTokenProvider.generateAccessToken(response.getMemberId(), "ADMIN");
//            String refreshToken = jwtTokenProvider.generateRefreshToken(response.getMemberId());
//            response.setAccessToken(accessToken);
//            response.setRefreshToken(refreshToken);
//
//            // 관리자 토큰 저장/접속시간 갱신
//            admin.setAccessToken(accessToken);
//            admin.setRefreshToken(refreshToken);
//            admin.setConnectData(LocalDateTime.now());
//            adminRepository.save(admin);
//
//            // 관리자 비밀번호 만료 체크는 기본 false 처리(정책에 따라 구현)
//            return ResponseEntity.ok(Map.of(
//                    "member", response,
//                    "isPasswordExpired", false
//            ));
//        }
//
//        // =========================
//        // ✅ 일반 회원 인증 경로 (기존 로직)
//        // =========================
//        MemberLoginResponseDto userRes = memberService.login(loginDto);
//
//        // 토큰 발급(USER)
//        String accessToken  = jwtTokenProvider.generateAccessToken(userRes.getMemberId(), "USER");
//        String refreshToken = jwtTokenProvider.generateRefreshToken(userRes.getMemberId());
//        userRes.setAccessToken(accessToken);
//        userRes.setRefreshToken(refreshToken);
//        userRes.setRole("USER"); // ✅ DTO에 role 채움
//        
//        // DB 저장(Refresh/Access)
//        MemberEntity member = memberRepository.findByMemberId(userRes.getMemberId())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
//        member.setAccessToken(accessToken);
//        member.setRefreshToken(refreshToken);
//        if (member.getPwUpdated() == null) member.setPwUpdated(LocalDateTime.now());
//        memberRepository.save(member);
//
//        userRes.setMemberNum(member.getMemberNum());	//강민씨 추가
//        
//        boolean isExpired = memberService.isPasswordExpired(member);
//
//        return ResponseEntity.ok(Map.of(
//                "member", userRes,
//                "isPasswordExpired", isExpired
//        ));
//    }
//	 
	 /**
     * 로그아웃
     * - 클라이언트: Authorization 헤더 + (쿠키 기반이면) withCredentials=true 로 호출
     * - 서버:
     *   - DB에 보관하던 refreshToken/accessToken 제거(있다면)
     *   - httpOnly refreshToken 쿠키 제거
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader(value = "Authorization", required = false) String tokenHeader,
            HttpServletResponse res
    ) {
        // 0) Authorization 헤더 형식 점검
        if (tokenHeader == null || !tokenHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            // 대소문자 무시 비교(regionMatches, ignoreCase=true)
            return ResponseEntity.badRequest().body("잘못된 토큰 형식입니다.");
        }

        // 1) "Bearer " 접두사 제거
        final String accessToken = tokenHeader.substring(7).trim();
        if (accessToken.isEmpty()) {
            return ResponseEntity.badRequest().body("잘못된 토큰 형식입니다.");
        }

        // 2) 액세스 토큰 유효성(서명/만료) 확인
        if (!jwtTokenProvider.validateToken(accessToken)) {
            // 만료/위변조 → 어차피 서버 상태 정리는 수행하고 200으로 내려도 되지만,
            // 일관성을 위해 401로 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        // 3) subject 추출(memberId 가정) → 회원 조회
        final String memberId = jwtTokenProvider.getMemberIdFromToken(accessToken);
        MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElse(null);

        // 4) 서버 보관 토큰 제거(DB 컬럼에 보관 중인 경우)
        //    - 프로젝트에 해당 컬럼이 없다면 이 부분은 생략 가능
        if (member != null) {
            member.setRefreshToken(null); // 재발급 차단
            member.setAccessToken(null);  // (선택) 저장해두던 accessToken도 정리
            memberRepository.save(member);
        }

        // 5) httpOnly refreshToken 쿠키 제거
        //    ⚠️ 발급 시 설정했던 path/domain/secure/httpOnly와 동일하게 맞춰줘야 정확히 지워짐
        Cookie refreshClear = new Cookie("refreshToken", null);
        refreshClear.setPath("/");        // 발급 시 path와 동일해야 함
        refreshClear.setMaxAge(0);        // 즉시 만료
        refreshClear.setHttpOnly(true);   // 보안 속성 유지
        // HTTPS 환경이면 true로 (로컬 http면 false)
        refreshClear.setSecure(false);
        // 특정 도메인을 썼다면 setDomain("example.com")도 동일하게 지정해야 함
        // refreshClear.setDomain("localhost"); // 필요 시
        res.addCookie(refreshClear);

        // 6) 응답
        return ResponseEntity.ok("로그아웃 완료");
    }

	//인증된 마이페이지 조회
	//현재 로그인한 사용자의 마이페이지를 조회합니다.
	//인증정보에서 사용자의 ID를 추출해 memberNum기반으로 조회
//	@GetMapping("/member/mypage")
//	public ResponseEntity<MemberMyPageResponseDto> myPage(){
//		//현재 인증 정보 가져오기
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		
//		//인증이 안된경우
//		if(auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//		}
//		
//		//인증된 사용자 ID 추출
//		String memberId = auth.getName();	//principal로 전달된 memberId
//		
//		//사용자 정보 조회(memberNum 얻기 위함)
//		MemberEntity member = memberRepository.findByMemberId(memberId)
//	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
//		
//		// ✅ 비밀번호 만료 시 403(FORBIDDEN)로 명확히 내려줌 (프론트에서 분기 처리 용이)
//	    if (memberService.isPasswordExpired(member)) {
//	        throw new ResponseStatusException(
//	            HttpStatus.FORBIDDEN,
//	            "비밀번호가 만료되어 마이페이지 접근이 제한됩니다."
//	        );
//	    }
//		//마이페이지 정보 반환
//		return ResponseEntity.ok(memberService.myPage(member.getMemberNum()));
//	}
	
	
	
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
