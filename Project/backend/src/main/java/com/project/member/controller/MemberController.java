package com.project.member.controller;


import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.project.admin.repository.AdminRepository;
import com.project.admin.service.AdminService;
import com.project.member.dto.AddressUpdateRequestDto;
import com.project.member.dto.MemberDeleteDto;
import com.project.member.dto.MemberFindPasswordRequestDto;
import com.project.member.dto.MemberMyPageResponseDto;
import com.project.member.dto.MemberPasswordUpdateRequestDto;
import com.project.member.dto.MemberSignUpRequestDto;
import com.project.member.dto.MemberSignUpResponseDto;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController	//JSON 응답 전용 컨트롤러
@RequiredArgsConstructor	//생성자주입
//@RequestMapping//클래스/메서드에 공통 URL 설정
@Slf4j
//요청수신, 로직호출, 결과응답
public class MemberController {
	private final MemberService memberService;
	private final MemberRepository memberRepository;
	private final AdminService adminService;
	private final AdminRepository adminRepository;

//	POST /members/signup → 회원가입
//	GET /members/mypage/numberNum → 마이페이지 조회
//	PUT /members/mypage/numberNum → 마이페이지 수정
//	PATCH /members/numberNum/sms-agree → SMS 동의 여부 변경
//	DELETE /members/numberNum → 회원 탈퇴
//	GET /members/find-id?memberName=홍길동&memberPhone=01012345678 → 아이디 찾기
//	GET /members/find-password?... → 비밀번호 찾기
//	PUT /members/update-password → 비밀번호 변경
//	GET /members/check-phone?phoneNum=01012345678 → 휴대폰 중복확인
	
	//회원가입
	@PostMapping("/signup")
	public ResponseEntity<MemberSignUpResponseDto> signup(@RequestBody MemberSignUpRequestDto dto){
		return ResponseEntity.ok(memberService.sigup(dto));
	}

	 // ✅ 아이디 중복 체크: 존재하면 409, 없으면 200
    @GetMapping("/check-id") // 최종 경로: (클래스 prefix) + "/check-id"
    public ResponseEntity<Map<String, String>> checkDuplicateId(@RequestParam("memberId") String memberId) {
        log.info("[check-id] memberId={}", memberId);

        // ⚠ null/blank 방어
        if (memberId == null || memberId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "memberId가 비어있습니다."));
        }

        try {
            boolean exists = memberService.isDuplicatedMemberId(memberId);
            if (exists) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 사용 중인 아이디입니다."));
            }
            return ResponseEntity.ok(Map.of("message", "사용 가능한 아이디입니다."));
        } catch (Exception e) {
            log.error("[check-id] ERROR: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "서버 오류: " + e.getClass().getSimpleName()));
        }
    }
   
    //인증된 마이페이지 수정(토큰으로 본인확인)
  	//현재 로그인한 사용자의 마이페이지 정보를 수정합니다.
  	//인증 정보를 기반으로 해당 사용자만 수정 가능하도록 합니다.
    @GetMapping("/member/mypage")
    public ResponseEntity<MemberMyPageResponseDto> myPage() {
        // 1) 인증 체크 → 비로그인: 401
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 2) principal에서 memberId 추출
        final String memberId = auth.getName(); // 토큰 subject가 memberId라고 가정

        // 3) 회원 조회 → 없으면 404로 명확히
        MemberEntity member = memberRepository.findByMemberId(memberId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."));

        // 4) 비밀번호 만료 → 403
        if (memberService.isPasswordExpired(member)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 만료되어 마이페이지 접근이 제한됩니다.");
        }

        try {
            // 5) 실제 마이페이지 조회 (서비스 내부 IllegalArgumentException 등은 404로 변환)
            MemberMyPageResponseDto body = memberService.myPage(member.getMemberNum());
            return ResponseEntity.ok(body);
        } catch (IllegalArgumentException iae) {
            // 서비스에서 "존재하지 않음" 등을 IllegalArgumentException으로 던질 수 있으므로 404로 변환
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, iae.getMessage());
        }
        // ※ 그 외 예외는 전역 예외 핸들러(있다면)에서 500으로 표준화하도록 두고,
        //   다음 스텝에서 서비스/DTO 보강으로 근본 원인을 줄입니다.
    }
    @PutMapping(value = "/member/mypage/address", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberMyPageResponseDto> updateMyAddress(@RequestBody AddressUpdateRequestDto dto) {
        // 1) 인증 검사
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 2) 본인 식별
        String memberId = auth.getName();
        MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."));

        // 3) 유효성(기본주소 필수)
        if (dto.getRoadAddress() == null || dto.getRoadAddress().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "기본주소(도로명)는 필수입니다.");
        }

        // 4) 갱신 후 최신 DTO 반환
        MemberMyPageResponseDto body = memberService.updateMyAddress(member.getMemberNum(), dto);
        return ResponseEntity.ok(body);
    }
//  	@PutMapping("/member/mypage")
//  	public ResponseEntity<MemberMyPageResponseDto> updateMyPage(@RequestBody MemberMyPageUpdateRequestDto dto){
//  		//현재 인증 정보 가져오기
//  		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//  		
//  		//인증이 안된경우
//  		if(auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
//  			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//  		}
//  		
//  		//인증된 사용자 ID 추출
//  		String memberId = auth.getName();	//principal로 전달된 memberId
//  		
//  		//사용자 정보 조회(memberNum 얻기 위함)
//  		MemberEntity member = memberRepository.findByMemberId(memberId)
//  	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
//  				
//  		//마이페이지 수정 로직 호출 및 결과 반환
//  		return ResponseEntity.ok(memberService.updateMyPage(member.getMemberNum(), dto));
//  	}
//  	
	//회원탈퇴
	@DeleteMapping("/mypage/del/{memberNum}")
	public ResponseEntity<MemberDeleteDto> memberOut(@PathVariable Long memberNum){
		return ResponseEntity.ok(memberService.memberOut(memberNum));
	}

	//아이디 찾기
	@GetMapping("/find-id")
	public ResponseEntity<String> findId(
			@RequestParam String memberName,
			@RequestParam String memberPhone){
		
		return ResponseEntity.ok(memberService.findMemberId(memberName, memberPhone));
	}
	
	//비밀번호 찾기
	@PostMapping("/find-pw")
	public ResponseEntity<String> findMemberPw(@RequestBody MemberFindPasswordRequestDto dto){
		String result = memberService.findMemberPw(dto.getMemberId(), dto.getMemberName(), dto.getMemberPhone());
		return ResponseEntity.ok(result);
	}

	//비밀번호 변경
	@PutMapping("/update-password")
	public ResponseEntity<String> updatePassword(@RequestBody MemberPasswordUpdateRequestDto dto){
		memberService.updatePassword(dto);
		return ResponseEntity.ok("비밀번호가 변경되었습니다.");
	}
	
	//휴대폰인증(중복확인)
	@GetMapping("/check-phone")
	public ResponseEntity<String> checkPhoneNumber(@RequestParam String phoneNum){
		return ResponseEntity.ok(memberService.checkPhoneNumber(phoneNum));
	}
}
