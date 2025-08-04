package com.project.volunteer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.volunteer.dto.VolunteerDetailDto;
import com.project.volunteer.service.VolunteerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/volunteer")
@RequiredArgsConstructor
public class VolunteerController {

    private final VolunteerService volunteerService;

    // 사용자 - 봉사 예약 상세 미리보기
    @GetMapping("/preview/{reserveCode}")
    public ResponseEntity<VolunteerDetailDto> getVolunteerPreview(@PathVariable Long reserveCode) {
        VolunteerDetailDto dto = volunteerService.getVolunteerDetailByReserveCode(reserveCode);
        return ResponseEntity.ok(dto);
    }
}