package com.project.reserve.controller;

import com.project.reserve.dto.*;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.service.ReserveService;

import com.project.land.dto.LandDetailDto;
import com.project.volunteer.dto.VolunteerDetailDto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reserve")
@RequiredArgsConstructor
public class AdminReserveController {

    private final ReserveService reserveService;

    // 전체 예약 목록 조회 
    @GetMapping("/all")
    public ResponseEntity<List<AdminReservationListDto>> getAllReservationsForAdmin() {
        List<AdminReservationListDto> list = reserveService.getAllReservationsForAdmin();
        return ResponseEntity.ok(list);
    }
    
    //놀이터예약 목록 조회
    @GetMapping("/land")
    public ResponseEntity<List<AdminReservationListDto>> getLandReservationsForAdmin() {
        List<AdminReservationListDto> list = reserveService.getLandReservationsForAdmin();
        return ResponseEntity.ok(list);
    } 
    
    //봉사예약 목록 조회
    @GetMapping("/volunteer")
    public ResponseEntity<List<AdminReservationListDto>> getVolunteerReservationsForAdmin() {
        List<AdminReservationListDto> list = reserveService.getVolunteerReservationsForAdmin();
        return ResponseEntity.ok(list);
    }
    
    // 놀이터 예약 검색
    @PostMapping("/land/search")
    public ResponseEntity<List<AdminReservationListDto>> searchLandReservations(@RequestBody AdminReservationSearchDto dto) {
        List<AdminReservationListDto> list = reserveService.searchLandReservationsForAdmin(dto);
        return ResponseEntity.ok(list);
    }

    // 봉사 예약 검색
    @PostMapping("/volunteer/search")
    public ResponseEntity<List<AdminReservationListDto>> searchVolunteerReservations(@RequestBody AdminReservationSearchDto dto) {
        List<AdminReservationListDto> list = reserveService.searchVolunteerReservationsForAdmin(dto);
        return ResponseEntity.ok(list);
    }

    // 놀이터 예약 상세 조회
    @GetMapping("/land/{reserveCode}")
    public ResponseEntity<LandDetailDto> getAdminLandReserveDetail(@PathVariable Long reserveCode) {
        LandDetailDto detail = reserveService.getAdminLandReserveDetail(reserveCode);
        return ResponseEntity.ok(detail);
    }

    // 봉사 예약 상세 조회 
    @GetMapping("/volunteer/{reserveCode}")
    public ResponseEntity<VolunteerDetailDto> getAdminVolunteerReserveDetail(@PathVariable Long reserveCode) {
        VolunteerDetailDto detail = reserveService.getAdminVolunteerReserveDetail(reserveCode);
        return ResponseEntity.ok(detail);
    }

    // 예약 상태 변경 (승인, 거절 등) 
    @PatchMapping("/{reserveCode}/state")
    public ResponseEntity<Void> updateReserveStateByAdmin(
            @PathVariable Long reserveCode,
            @RequestBody Map<String, String> body) {

        ReserveState newState = ReserveState.valueOf(body.get("reserveState"));
        reserveService.updateReserveStateByAdmin(reserveCode, newState);
        return ResponseEntity.ok().build();
    }
}