package com.project.reserve.controller;

import com.project.reserve.dto.*;
import com.project.reserve.service.ReserveService;
import com.project.reserve.entity.ReserveState;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reserve")
@RequiredArgsConstructor
public class AdminReserveController {

    private final ReserveService reserveService;

    
    //전체 예약 목록을 reserveType(예: 놀이터/봉사활동) 기준으로 필터링 조회
    @GetMapping("/type")
    public ResponseEntity<List<ReserveResponseDto>> getReservesByType(@RequestParam("type") int type) {
        List<ReserveResponseDto> list = reserveService.getReservesByType(type);
        return ResponseEntity.ok(list);
    }

    //특정 날짜 기준으로 예약 목록 조회
     
    @GetMapping("/date")
    public ResponseEntity<List<ReserveResponseDto>> getReservesByDate(@RequestParam("date") LocalDate date) {
        List<ReserveResponseDto> list = reserveService.getReservesByDate(date);
        return ResponseEntity.ok(list);
    }

    
    //예약 상태를 변경 (승인, 거절, 완료 등)
     
    @PatchMapping("/state")
    public ResponseEntity<Void> updateReserveState(@RequestBody AdminReservationUpdateDto updateDto) {
        reserveService.updateReserveState(updateDto.getReserveCode(), updateDto.getReserveState());
        return ResponseEntity.ok().build();
    }

    //특정 회원의 예약 내역 확인 (필요한 경우만 사용)
    @GetMapping("/member/{memberName}")
    public ResponseEntity<List<ReserveResponseDto>> getReservesByMember(@PathVariable Long memberNum) {
        List<ReserveResponseDto> list = reserveService.getReservesByMember(memberNum);
        return ResponseEntity.ok(list);
    }
}