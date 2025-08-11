package com.project.common.controller;

import com.project.common.dto.TimeSlotDto;
import com.project.common.entity.TimeType;
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

    // 사용자 - 예약 유형별 시간대 조회
    @GetMapping("/{type}")
    public ResponseEntity<List<TimeSlotDto>> getTimeSlotsByType(@PathVariable String type) {
        try {
            TimeType timeType = TimeType.valueOf(type.toUpperCase()); // enum 변환
            List<TimeSlotDto> result = timeSlotService.getTimeSlotsByType(timeType);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // 잘못된 type 값 ㅇㅇ
        }
    }
}