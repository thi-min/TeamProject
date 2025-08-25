package com.project.volunteer.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.volunteer.dto.VolunteerCountDto;
import com.project.volunteer.dto.VolunteerDetailDto;
import com.project.volunteer.service.VolunteerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/volunteer")
@RequiredArgsConstructor
public class AdminVolunteerController {

    private final VolunteerService volunteerService;

    // 관리자 - 봉사 예약 인원 수 조회 (날짜 + 시간대 기준)
    @GetMapping("/count")
    public ResponseEntity<VolunteerCountDto> getVolunteerCountInfo(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate volDate,
            @RequestParam("timeSlotId") Long timeSlotId) {
        VolunteerCountDto countInfo = volunteerService.getVolunteerCountInfo(volDate, timeSlotId);
        return ResponseEntity.ok(countInfo);
    }

    // 관리자 - 봉사 예약 상세 미리보기
    @GetMapping("/preview/{reserveCode}")
    public ResponseEntity<VolunteerDetailDto> getVolunteerPreview(@PathVariable Long reserveCode) {
        VolunteerDetailDto dto = volunteerService.getVolunteerDetailByReserveCode(reserveCode);
        return ResponseEntity.ok(dto);  //xptmxm
    }
}