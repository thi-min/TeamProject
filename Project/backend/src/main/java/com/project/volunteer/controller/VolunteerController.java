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
@RequestMapping("/api/admin/volunteer")
@RequiredArgsConstructor
public class VolunteerController {

    private final VolunteerService volunteerService;

    //관리자 - 예약 인원 수 조회 (날짜 + 시간대 기준)
    @GetMapping("/count")
    public ResponseEntity<VolunteerCountDto> getVolunteerCountInfo(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate volDate,
            @RequestParam("time") String volTime) {

        VolunteerCountDto countInfo = volunteerService.getVolunteerCountInfo(volDate, volTime);
        return ResponseEntity.ok(countInfo);
    }
    
    @GetMapping("/preview/{reserveCode}")
    public ResponseEntity<VolunteerDetailDto> getVolunteerPreview(@PathVariable Long reserveCode) {
        VolunteerDetailDto dto = volunteerService.getVolunteerDetailByReserveCode(reserveCode);
        return ResponseEntity.ok(dto);
    }
}