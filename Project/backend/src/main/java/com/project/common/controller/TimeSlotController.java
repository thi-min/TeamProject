package com.project.common.controller;

import com.project.common.dto.TimeSlotDto;
import com.project.common.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-slots")  // 사용자용 prefix
@RequiredArgsConstructor
public class TimeSlotController { // 사용자용이므로 클래스명도 구분 추천

    private final TimeSlotService timeSlotService;

    // 사용자 - 놀이터 시간대 조회
    @GetMapping("/land")
    public ResponseEntity<List<TimeSlotDto>> getLandTimeSlots() {
        List<TimeSlotDto> result = timeSlotService.getLandTimeSlots();
        return ResponseEntity.ok(result);
    }

    // 사용자 - 봉사 시간대 조회
    @GetMapping("/volunteer")
    public ResponseEntity<List<TimeSlotDto>> getVolunteerTimeSlots() {
        List<TimeSlotDto> result = timeSlotService.getVolunteerTimeSlots();
        return ResponseEntity.ok(result);
    }
}