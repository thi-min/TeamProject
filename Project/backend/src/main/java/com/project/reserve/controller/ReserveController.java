package com.project.reserve.controller;

import com.project.land.dto.LandDetailDto;
import com.project.reserve.dto.FullReserveRequestDto;
import com.project.reserve.dto.ReserveRequestDto;
import com.project.reserve.dto.ReserveResponseDto;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.service.ReserveService;
import com.project.volunteer.dto.VolunteerDetailDto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reserve")
@RequiredArgsConstructor
public class ReserveController {

    private final ReserveService reserveService;

    //사용자 - 예약 생성
    @PostMapping
    public ResponseEntity<Long> createReserve(@RequestBody FullReserveRequestDto fullRequestDto) {
        Long reserveCode = reserveService.createReserve(fullRequestDto);
        return ResponseEntity.ok(reserveCode);
    }
    
    //사용자 - 회원 예약목록 조회 (마이페이지)
    @GetMapping("/member")
    public ResponseEntity<List<ReserveResponseDto>> getMyReserves(@RequestParam("memberNum") Long memberNum) {
        List<ReserveResponseDto> list = reserveService.getReservesByMember(memberNum);
        return ResponseEntity.ok(list);
    }
    
    //사용자 - 본인 예약 취소
    @DeleteMapping("/{reserveCode}/cancel")
    public ResponseEntity<Void> cancelMyReserve(
            @PathVariable Long reserveCode, //101/cancel
            @RequestParam Long memberNum	//cancel?memberNum=5
    ) {
        reserveService.memberCancelReserve(reserveCode, memberNum);
        return ResponseEntity.ok().build();
    }
    //ex)DELETE /api/reserve/101/cancel?memberNum=5 

    //사용자 - 놀이터 예약 상세조회(마이페이지)
    @GetMapping("/land/{reserveCode}/member/{memberNum}")
    public ResponseEntity<LandDetailDto> getMyLandReserveDetail(
            @PathVariable Long reserveCode,
            @PathVariable Long memberNum) {
        LandDetailDto detail = reserveService.getMemberLandReserveDetail(reserveCode, memberNum);
        return ResponseEntity.ok(detail);
    }
    
    //사용자 - 봉사 예약 상세조회(마이페이지)
    @GetMapping("/volunteer/{reserveCode}/member/{memberNum}")
    public ResponseEntity<VolunteerDetailDto> getMyVolunteerReserveDetail(
            @PathVariable Long reserveCode,
            @PathVariable Long memberNum) {
        VolunteerDetailDto detail = reserveService.getMemberVolunteerReserveDetail(reserveCode, memberNum);
        return ResponseEntity.ok(detail);
    }
    
    //예약 유형별 탭기능  (봉사 / 놀이터)
    @GetMapping("/type")
    public ResponseEntity<List<ReserveResponseDto>> getMyReservesByType(
    		@RequestParam("memberNum") Long memberNum,
            @RequestParam("type") int type) {
        List<ReserveResponseDto> list = reserveService.getReservesByMemberAndType(memberNum, type);
        return ResponseEntity.ok(list);
    }
    
}