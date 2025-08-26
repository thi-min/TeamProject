package com.project.volunteer.controller;

import com.project.volunteer.dto.VolunteerCountDto;
import com.project.volunteer.dto.VolunteerDetailDto;
import com.project.volunteer.service.VolunteerService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/volunteer")
@RequiredArgsConstructor
public class VolunteerController {

    private final VolunteerService volunteerService;
    
    
    // 사용자 - 봉사 시간대 전체 조회 + 예약 인원 포함
    @GetMapping("/timeslots")
    public ResponseEntity<List<VolunteerCountDto>> getVolunteerTimeSlotsWithCount(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate volDate,
            @RequestParam("memberNum") Long memberNum) {

        List<VolunteerCountDto> result = volunteerService.getVolunteerTimeSlotsWithCount(volDate, memberNum);
        return ResponseEntity.ok(result);
    }
    
    // 사용자 - 봉사 예약 상세정보 보기
    @GetMapping("/detail/{reserveCode}")
    public ResponseEntity<VolunteerDetailDto> getVolunteerPreview(
    		@PathVariable Long reserveCode) {
        VolunteerDetailDto dto = volunteerService.getVolunteerDetailByReserveCode(reserveCode);
        return ResponseEntity.ok(dto);
    }
}