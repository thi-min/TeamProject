package com.project.member.controller;


import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.admin.repository.AdminRepository;
import com.project.admin.service.AdminService;
import com.project.member.dto.MemberDeleteDto;
import com.project.member.dto.MemberFindPasswordRequestDto;
import com.project.member.dto.MemberIdCheckResponseDto;
import com.project.member.dto.MemberPasswordUpdateRequestDto;
import com.project.member.dto.MemberSignUpRequestDto;
import com.project.member.dto.MemberSignUpResponseDto;
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
	
	//로그인 토큰으로 구현 예정
//	@GetMapping("/login")
//	public ResponseEntity<MemberLoginResponseDto> login(@RequestBody MemberLoginRequestDto loginDto, HttpSession session){
//		MemberLoginResponseDto response = memberService.login(loginDto);
//		
//		//로그인 응답에 token 포함
//		return ResponseEntity.ok(response);
//	}
	
	
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
	
//	//아이디 중복 체크
//	@GetMapping("/check-id")
//	public ResponseEntity<MemberIdCheckResponseDto> checkDuplicateId(@RequestParam String memberId) {
//	    return ResponseEntity.ok(memberService.checkDuplicateMemberId(memberId));
//	}
    
	// 아이디 중복 체크
//    @GetMapping("/check-id")
//    public ResponseEntity<?> checkDuplicateId(@RequestParam("memberId") String memberId) {
//        try {
//            log.info("[check-id] memberId={}", memberId); // ✅ 파라미터 확인
//            MemberIdCheckResponseDto dto = memberService.checkDuplicateMemberId(memberId);
//            log.info("[check-id] exists={} message={}", dto.isExists(), dto.getMessage());
//            return ResponseEntity.ok(dto);
//        } catch (Exception e) {
//            // ✅ 어떤 예외가 나는지 로그로 캡처
//            log.error("[check-id] ERROR: {}", e.getMessage(), e);
//            // 프론트 디버깅 편의상 200으로 메시지 내려줘도 되고, 500 유지해도 OK
//            return ResponseEntity.internalServerError().body(
//                new MemberIdCheckResponseDto(true, "서버 오류: " + e.getClass().getSimpleName())
//            );
//        }
//    }

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
    
	//AuthController에 구현해서 주석처리
//	//마이페이지 조회
//	@GetMapping("/mypage/{memberNum}")
//	//@PathVariable	URL 경로 매핑
//	public ResponseEntity<MemberMyPageResponseDto> myPage(@PathVariable Long memberNum){
//		return ResponseEntity.ok(memberService.myPage(memberNum));
//	}
//	
//	//마이페이지 수정 + SMS 수신 동의 여부
//	@PutMapping("/mypage/{memberNum}")
//	public ResponseEntity<MemberMyPageResponseDto> updateMyPage(
//			@PathVariable Long memberNum,
//			@RequestBody MemberMyPageUpdateRequestDto dto){
//		return ResponseEntity.ok(memberService.updateMyPage(memberNum, dto));
//	}
	
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
