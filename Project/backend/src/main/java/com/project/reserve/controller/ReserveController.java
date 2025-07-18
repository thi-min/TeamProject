package com.project.reserve.controller;

import com.project.reserve.dto.ReserveRequestDto;
import com.project.reserve.dto.ReserveResponseDto;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.service.ReserveService;
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

    //예약 생성
    @PostMapping
    public ResponseEntity<Long> createReserve(@RequestBody ReserveRequestDto requestDto) {
        Long reserveCode = reserveService.createReserve(requestDto);
        return ResponseEntity.ok(reserveCode);
    }

    //사용자 - 본인 예약 취소
    @DeleteMapping("/{reserveCode}/cancel")
    public ResponseEntity<Void> cancelMyReserve(
            @PathVariable Long reserveCode,
            @RequestParam Long memberNum
    ) {
        reserveService.memberCancelReserve(reserveCode, memberNum);
        return ResponseEntity.ok().build();
    }

    //날짜별 예약 조회(기간)
    @GetMapping("/date")
    public ResponseEntity<List<ReserveResponseDto>> getReservesByDate(@RequestParam("date") LocalDate date) {
        List<ReserveResponseDto> list = reserveService.getReservesByDate(date);
        return ResponseEntity.ok(list);
    }

    //회원 예약 조회 (마이페이지)
    @GetMapping("/member")
    public ResponseEntity<List<ReserveResponseDto>> getMyReserves(@RequestParam("memberNum") Long memberNum) {
        List<ReserveResponseDto> list = reserveService.getReservesByMember(memberNum);
        return ResponseEntity.ok(list);
    }
}