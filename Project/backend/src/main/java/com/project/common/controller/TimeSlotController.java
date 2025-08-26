package com.project.common.controller;

import com.project.common.dto.TimeSlotDto;
import com.project.common.entity.TimeType;
import com.project.common.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeslots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    // 사용자 - 예약 유형별 시간대 조회
    @GetMapping("/{type}")
    public ResponseEntity<List<TimeSlotDto>> getTimeSlotsByType(@PathVariable String type) {
        try {
            List<TimeSlotDto> result;
            
            if ("ALL".equalsIgnoreCase(type)) {
                result = timeSlotService.getAllTimeSlots();
            } else {
                TimeType timeType = TimeType.valueOf(type.toUpperCase());
                result = timeSlotService.getTimeSlotsByType(timeType);
            }

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
}