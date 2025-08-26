package com.project.common.controller;

import com.project.common.dto.TimeSlotDto;
import com.project.common.entity.TimeType;
import com.project.common.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/timeslots") // 관리자 전용
@RequiredArgsConstructor
public class AdminTimeSlotController {

    private final TimeSlotService timeSlotService;

    // 관리자 - 예약 유형별 시간대 조회
    @GetMapping("/{type}")
    public ResponseEntity<List<TimeSlotDto>> getTimeSlotsByType(@PathVariable String type) {
        try {
            TimeType timeType = TimeType.valueOf(type.toUpperCase());
            List<TimeSlotDto> result = timeSlotService.getTimeSlotsByType(timeType);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 시간대 추가
    @PostMapping
    public ResponseEntity<Void> addTimeSlot(@RequestBody TimeSlotDto dto) {
        timeSlotService.addTimeSlot(dto);
        return ResponseEntity.ok().build();
    }

    // 시간대 수정
    @PutMapping("/{timeSlotId}")
    public ResponseEntity<Void> updateTimeSlot(@PathVariable Long timeSlotId,
                                               @RequestBody TimeSlotDto dto) {
        timeSlotService.updateTimeSlot(timeSlotId, dto);
        return ResponseEntity.ok().build();
    }

    // 시간대 삭제
    @DeleteMapping("/{timeSlotId}")
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable Long timeSlotId) {
        timeSlotService.deleteTimeSlot(timeSlotId);
        return ResponseEntity.ok().build();
    }

    // label 중복 체크
    @GetMapping("/check-duplicate")
    public ResponseEntity<Boolean> isDuplicateLabel(@RequestParam("label") String label) {
        boolean isDuplicate = timeSlotService.isDuplicateLabel(label);
        return ResponseEntity.ok(isDuplicate);
    }
}