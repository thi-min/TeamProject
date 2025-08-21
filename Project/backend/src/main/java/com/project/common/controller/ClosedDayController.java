package com.project.common.controller;

import com.project.common.dto.ClosedDayRequestDto;
import com.project.common.dto.ClosedDayResponseDto;
import com.project.common.service.ClosedDayService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/closed-days")
@RequiredArgsConstructor
public class ClosedDayController {

    private final ClosedDayService closedDayService;
    
    // 공휴일 등록(연도)
    @PostMapping("/holidays/{year}")
    public ResponseEntity<String> registerHolidays(@PathVariable int year) {
        closedDayService.registerHolidays(year);
        return ResponseEntity.ok(year + "년 공휴일 등록 완료");
    }
    
    // 휴무일 조회(특정 년도, 특정 월)
    @GetMapping
    public ResponseEntity<List<ClosedDayResponseDto>> getClosedDays(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(closedDayService.getClosedDays(year, month));
    }
    
    // ✅ 휴무일 등록/수정
    @PostMapping
    public void setClosedDay(@RequestBody ClosedDayRequestDto dto) {
        closedDayService.setClosedDay(dto);
    }

    // ✅ 휴무일 삭제
    @DeleteMapping("/{date}")
    public void deleteClosedDay(@PathVariable String date) {
        LocalDate targetDate = LocalDate.parse(date);
        closedDayService.deleteClosedDay(targetDate);
    }
   
}
