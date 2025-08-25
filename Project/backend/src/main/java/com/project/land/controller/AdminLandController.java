package com.project.land.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.land.dto.LandCountDto;
import com.project.land.dto.LandDetailDto;
import com.project.land.entity.LandType;
import com.project.land.service.LandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/land")
@RequiredArgsConstructor
public class AdminLandController {

    private final LandService landService;

    // 관리자용 - 단일 시간대 예약 수 조회
    @GetMapping("/count")
    public ResponseEntity<LandCountDto> getLandCountInfo(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate landDate,
            @RequestParam("timeSlotId") Long timeSlotId,
            @RequestParam("type") LandType landType) {
        LandCountDto countInfo = landService.getLandCountForSlot(landDate, timeSlotId, landType);
        return ResponseEntity.ok(countInfo);
    }

}