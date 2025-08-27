package com.project.land.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.land.dto.LandCountDto;
import com.project.land.dto.LandDetailDto;
import com.project.land.entity.LandType;
import com.project.land.service.LandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/land")
@RequiredArgsConstructor
public class LandController {

    private final LandService landService;
    
    
    // 사용자용 - 시간대 전체 조회 + 예약 마리수 포함
    @GetMapping("/timeslots")
    public ResponseEntity<List<LandCountDto>> getLandTimeSlotsWithCount(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate landDate,
            @RequestParam("memberNum") Long memberNum,
            @RequestParam("landType") LandType landType) {
        List<LandCountDto> result = landService.getLandTimeSlotsWithCount(landDate, memberNum, landType);
        return ResponseEntity.ok(result);
    }
    
    // 사용자 - 월별 예약 마감 확인
    @GetMapping("/timeslots/month")
    public ResponseEntity<Map<LocalDate, List<LandCountDto>>> getLandTimeSlotsByMonth(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        Map<LocalDate, List<LandCountDto>> result = landService.getLandTimeSlotsByMonth(year, month);
        return ResponseEntity.ok(result);
    }
    
    // 놀이터상세정보 보기 (+결제금액)
    @GetMapping("/detail/{reserveCode}")
    public ResponseEntity<LandDetailDto> getLandDetail(
            @PathVariable Long reserveCode) {
        LandDetailDto dto = landService.getLandDetailByReserveCode(reserveCode);
        return ResponseEntity.ok(dto);
    }
}