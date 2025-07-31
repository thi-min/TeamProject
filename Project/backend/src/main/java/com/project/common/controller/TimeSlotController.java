package com.project.common.controller;

import com.project.common.dto.TimeSlotDto;

import com.project.common.service.TimeSlotService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-slots")  // 공통 URI
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    // 관리자 - 시간대 추가
    @PostMapping
    public ResponseEntity<Void> addTimeSlot(@RequestBody TimeSlotDto dto) {
        timeSlotService.addTimeSlot(dto);
        return ResponseEntity.ok().build();
    }

    // 관리자 - 시간대 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTimeSlot(@PathVariable Long id,
                                               @RequestBody TimeSlotDto dto) {
        timeSlotService.updateTimeSlot(id, dto);
        return ResponseEntity.ok().build();
    }

    // 관리자 - 시간대 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeSlot(@PathVariable Long id) {
        timeSlotService.deleteTimeSlot(id);
        return ResponseEntity.ok().build();
    }

    // 관리자 - 중복 체크 (label만)
    @GetMapping("/check-duplicate")
    public ResponseEntity<Boolean> isDuplicateLabel(@RequestParam("label") String label) {
        boolean isDuplicate = timeSlotService.isDuplicateLabel(label);
        return ResponseEntity.ok(isDuplicate);
    }
}