package com.project.common.controller;

import com.project.common.dto.ClosedDayRequestDto;
import com.project.common.service.ClosedDayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/admin/closed-days")  // 관리자용 prefix
@RequiredArgsConstructor
public class AdminClosedDayController {

    private final ClosedDayService closedDayService;

    // 휴무일 등록 또는 수정
    @PostMapping
    public ResponseEntity<Void> setClosedDay(@RequestBody ClosedDayRequestDto dto) {
        closedDayService.setClosedDay(dto);
        return ResponseEntity.ok().build();
    }

    // 휴무일 삭제
    @DeleteMapping("/{date}")
    public ResponseEntity<Void> deleteClosedDay(
        @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        closedDayService.deleteClosedDay(date);
        return ResponseEntity.ok().build();
    }

    // 명절 및 공휴일 자동 등록
    @PostMapping("/auto")
    public ResponseEntity<Void> registerAuto(@RequestParam int year) {
        closedDayService.registerHolidays(year);
        return ResponseEntity.ok().build();
    }
}