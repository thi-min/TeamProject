package com.project.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.admin.dto.AdminLoginRequestDto;
import com.project.admin.dto.AdminLoginResponseDto;
import com.project.admin.dto.AdminMemberDetailResponseDto;
import com.project.admin.dto.AdminMemberListResponseDto;
import com.project.admin.dto.AdminMemberUpdateRequestDto;
import com.project.admin.dto.AdminPasswordUpdateRequestDto;
import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
import com.project.admin.service.AdminService;
import com.project.common.dto.PageRequestDto;
import com.project.common.dto.PageResponseDto;
import com.project.common.jwt.JwtTokenProvider;
import com.project.member.entity.MemberState;

import lombok.RequiredArgsConstructor;

@RestController	//JSON 응답 전용 컨트롤러
@RequiredArgsConstructor	//생성자주입
@RequestMapping	("/admin")//클래스/메서드에 공통 URL 설정
public class AdminController {
	
	private final AdminService adminService;
	private final AdminRepository adminRepository;
	private final JwtTokenProvider jwtTokenProvider;
	
	//관리자 로그인
	//param : dto 관리자 로그인 요청 정보(아이디, 비밀번호)
	//return : 관리자 정보 + 로그인 성공 메시지 + 토큰
	@PostMapping("login")
	public AdminLoginResponseDto login(@RequestBody AdminLoginRequestDto dto) {
		
		//1. 아이디로 관리자 먼저 조회
	    AdminEntity admin = adminRepository.findFirstByAdminId(dto.getAdminId())
	        .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
	    
	    //2. 비밀번호는 passwordEncoder로 암호화 매칭
	    if (!new BCryptPasswordEncoder().matches(dto.getAdminPw(), admin.getAdminPw())) {
	        throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
	    }
	    
	    // ✅ role: ADMIN 포함한 토큰 발급
	    String accessToken = jwtTokenProvider.generateAccessToken(admin.getAdminId(), "ADMIN");
	    String refreshToken = jwtTokenProvider.generateRefreshToken(admin.getAdminId());

	    // ✅ refreshToken 저장
	    admin.setRefreshToken(refreshToken);
	    adminRepository.save(admin);

	    return AdminLoginResponseDto.builder()
	            .adminId(admin.getAdminId())
	            .adminEmail(admin.getAdminEmail())
	            .accessToken(accessToken)	// ⬅️ 발급한 토큰 포함
	            .refreshToken(refreshToken)
	            .message("관리자 로그인 성공")
	            .build();
	}

	
	//관리자 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestHeader("Authorization") String tokenHeader) {
	    if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
	        return ResponseEntity.badRequest().body("잘못된 토큰 형식입니다.");
	    }

	    String token = tokenHeader.substring(7);
	    String adminId = jwtTokenProvider.getMemberIdFromToken(token); // 관리자도 getMemberIdFromToken 쓰는 경우


	    AdminEntity admin = adminRepository.findFirstByAdminId(adminId)
	            .orElseThrow(() -> new IllegalArgumentException("관리자 계정을 찾을 수 없습니다."));

	    // Refresh 토큰 DB에 저장한 경우
	    admin.setRefreshToken(null);
	    adminRepository.save(admin);

	    return ResponseEntity.ok("관리자 로그아웃 성공");
	}

	
	//관리자 비밀번호 변경
	//param dto 비밀번호 변경 요청 정보(아이디, 현재 비밀번호, 새 비밀번호)
	//return 성공 메시지
	@PutMapping("/update-password")
	public ResponseEntity<String> updatePassword(@RequestBody AdminPasswordUpdateRequestDto dto){
		adminService.updatePassword(dto.getAdminId(), dto);
		return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
	}
	
	//전체 회원목록 조회(페이징 + 검색 포함)
	//param pageRequestDto 페이지번호, 크기, 검색키워드
	//return pageResponseDto 형태로 페이지 정보 + 회원 목록 반환
	@GetMapping("/membersList")
	public ResponseEntity<PageResponseDto<AdminMemberListResponseDto>> getPagedMembers(PageRequestDto pageRequestDto){
		PageResponseDto<AdminMemberListResponseDto> page = adminService.getMemberList(pageRequestDto);
		
		return ResponseEntity.ok(page);
	}
	
	//회원 상세 조회(관리자용
	//param : memberNum 조회할 회원 번호
	//return : AdminMemberDetailResponseDto
	@GetMapping("/membersList/{memberNum}")
	public ResponseEntity<AdminMemberDetailResponseDto> getMemberDetail(@PathVariable Long memberNum){
		AdminMemberDetailResponseDto response = adminService.adminMemberDetailView(memberNum);
		return ResponseEntity.ok(response);
	}
	
	//회원정보수정
	//param : memberNum 수정할 회원 번호
	//return : AdminMemberUpdateRequestDto
	@PutMapping("/membersList/{memberNum}")
	public ResponseEntity<AdminMemberUpdateRequestDto> updateMemberState(
			@PathVariable Long memberNum,	//경로에서 memberNum을 받음
	        @RequestParam MemberState memberState,
	        @RequestParam Boolean memberLock) {
	    
	    AdminMemberUpdateRequestDto result = adminService.adminMemberStateChange(memberNum, memberState, memberLock);
	    return ResponseEntity.ok(result);
	}

//	관리자 로그인	POST	/admin/login	로그인 후 토큰 발급 (토큰은 response DTO에 포함 예정)
//	비밀번호 변경	PUT	/admin/password	본인 비밀번호 변경
//	회원 목록 조회	GET	/admin/members	전체 회원 목록 조회 (페이지네이션 포함 시 쿼리 파라미터 활용)
//	회원 상세 조회	GET	/admin/members/{memberNum}	특정 회원 상세 정보 조회
//	회원 상태 변경	PATCH	/admin/members/{memberNum}/state	REST/OUT/ACTIVE 상태 변경
//	회원 잠금 상태 변경	PATCH	/admin/members/{memberNum}/lock	계정 잠금/해제
//	(선택) 관리자 로그아웃	POST	/admin/logout	필요 시 리프레시 토큰 삭제 등 처리
}
