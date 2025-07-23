package com.project.land.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.land.dto.LandCountDto;
import com.project.land.service.LandService;

import lombok.RequiredArgsConstructor;

@RestController	//JSON반환 하는 REST API 컨트롤러
@RequestMapping("/api/admin/land")
@RequiredArgsConstructor
public class LandController {

    private final LandService landService; //

    // 관리자 - 예약 마릿수 조회 (날짜 + 시간대 기준)
    @GetMapping("/count")
    public ResponseEntity<LandCountDto> getLandCountInfo(
    		//날짜
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate landDate,
            //시간대
            @RequestParam("time") String landTime) { 
    	
    	//서비스에서 예약 수 조회 및 DTO 생성
        LandCountDto countInfo = landService.getLandCountInfo(landDate, landTime);
        return ResponseEntity.ok(countInfo); //JSON 형태로 응답 반환
    }
}