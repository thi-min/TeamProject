package com.project.reserve.controller;

import com.project.land.dto.LandDetailDto;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.reserve.dto.FullReserveRequestDto;
import com.project.reserve.dto.ReserveCompleteResponseDto;
import com.project.reserve.dto.ReserveRequestDto;
import com.project.reserve.dto.ReserveResponseDto;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.service.ReserveService;
import com.project.volunteer.dto.VolunteerDetailDto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reserve")
@RequiredArgsConstructor
public class ReserveController {

    private final ReserveService reserveService;
    private final MemberRepository memberRepository; 

    //사용자 - 예약 생성
    @PostMapping
    public ResponseEntity<ReserveCompleteResponseDto> createReserve(@RequestBody FullReserveRequestDto fullRequestDto) {
        ReserveCompleteResponseDto response = reserveService.createReserve(fullRequestDto);
        return ResponseEntity.ok(response);
    }
    
    //사용자 - 회원 예약목록 조회 (마이페이지)
    @GetMapping("/my")//reserveAllList
    public ResponseEntity<List<ReserveResponseDto>> getMyReserves(@RequestParam("memberNum") Long memberNum) {
        List<ReserveResponseDto> list = reserveService.getReservesByMember(memberNum);
        return ResponseEntity.ok(list);
    }
    
    //사용자 - 본인 예약 취소
    @DeleteMapping("/{reserveCode}/cancel")
    public ResponseEntity<Void> cancelMyReserve(
            @PathVariable Long reserveCode,
            Authentication authentication
    ) {
        String memberId = authentication.getName();
        MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        reserveService.memberCancelReserve(reserveCode, member.getMemberNum());
        return ResponseEntity.ok().build();
    }
    

    //사용자 - 놀이터 예약 상세조회(마이페이지)
    @GetMapping("/land/{reserveCode}")
    public ResponseEntity<LandDetailDto> getMyLandReserveDetail(
            @PathVariable Long reserveCode,
            Authentication authentication) {
        String memberId = authentication.getName(); // 로그인된 ID
        MemberEntity member = memberRepository.findByMemberId(memberId)
        		.orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        Long memberNum = member.getMemberNum();

        LandDetailDto detail = reserveService.getMemberLandReserveDetail(reserveCode, memberNum);
        return ResponseEntity.ok(detail);
    }
    
    //사용자 - 봉사 예약 상세조회(마이페이지)
    @GetMapping("/volunteer/{reserveCode}")
    public ResponseEntity<VolunteerDetailDto> getMyVolunteerReserveDetail(
            @PathVariable Long reserveCode,
            Authentication authentication) {   	
    	String memberId = authentication.getName(); // 로그인된 ID
 
        MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        Long memberNum = member.getMemberNum();

        VolunteerDetailDto detail = reserveService.getMemberVolunteerReserveDetail(reserveCode, memberNum);
        return ResponseEntity.ok(detail);
    }
    
    //예약 유형별 탭기능  (봉사 / 놀이터)
    @GetMapping("/my/type")
    public ResponseEntity<List<ReserveResponseDto>> getMyReservesByType(
    		@RequestParam("memberNum") Long memberNum,
            @RequestParam("type") int type) {
        List<ReserveResponseDto> list = reserveService.getReservesByMemberAndType(memberNum, type);
        return ResponseEntity.ok(list);
    }
    
    // formpage -> confirmpage 넘어갈때 예약 중복검사
    @GetMapping("/check-duplicate")
    public ResponseEntity<Boolean> checkDuplicate(
            @RequestParam Long memberNum,
            @RequestParam LocalDate date,
            @RequestParam Long timeSlotId,
            @RequestParam String type // "LAND" or "VOLUNTEER"
    ) {
        boolean exists;
        if ("LAND".equalsIgnoreCase(type)) {
            exists = reserveService.existsLandDuplicate(memberNum, date, timeSlotId);
        } else if ("VOLUNTEER".equalsIgnoreCase(type)) {
            exists = reserveService.existsVolunteerDuplicate(memberNum, date, timeSlotId);
        } else {
            throw new IllegalArgumentException("예약 유형이 잘못되었습니다.");
        }
        return ResponseEntity.ok(exists);
    }
    
}