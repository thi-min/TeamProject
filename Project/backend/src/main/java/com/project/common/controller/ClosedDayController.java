package com.project.common.controller;

import com.project.common.dto.ClosedDayResponseDto;
import com.project.common.service.ClosedDayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/closed-days")  // 사용자용 prefix
@RequiredArgsConstructor
public class ClosedDayController {

    private final ClosedDayService closedDayService;

    // 1. 특정 기간 내 휴무일 조회 (사용자 달력용)
    @GetMapping
    public ResponseEntity<List<ClosedDayResponseDto>> getClosedDays(
        @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        List<ClosedDayResponseDto> result = closedDayService.getClosedDaysInPeriod(start, end);
        return ResponseEntity.ok(result);
    }

    // 2. 휴무일 여부 확인 (예약 시점 체크용)
    @GetMapping("/check")
    public ResponseEntity<Boolean> isClosed(
        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(closedDayService.isClosed(date));
    }
}