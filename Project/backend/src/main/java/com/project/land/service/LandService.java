package com.project.land.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.project.common.dto.TimeSlotDto;
import com.project.common.entity.TimeSlot;
import com.project.land.dto.LandCountDto;
import com.project.land.dto.LandDetailDto;
import com.project.land.dto.LandRequestDto;
import com.project.land.entity.LandType;
import com.project.reserve.entity.Reserve;

public interface LandService {
	//예약코드에 따라 상세보기페이지
    LandDetailDto getLandDetailByReserveCode(Long reserveCode);
    
    //예약 생성시 land정보와 reserve정보 합칠때 사용
    void createLand(Reserve reserve, LandRequestDto landDto, TimeSlot timeSlot);
    
    //  단일 시간대 예약 마릿수 (관리자용)
    LandCountDto getLandCountForSlot(LocalDate landDate, Long timeSlotId, LandType landType);

    //  전체 시간대별 현황 조회 (사용자용)
    List<LandCountDto> getLandTimeSlotsWithCount(LocalDate landDate, LandType landType);
    List<LandCountDto> getLandTimeSlotsWithCount(LocalDate landDate, Long memberNum, LandType landType);
    
    // 월별 정원 체크(사용자용)
    Map<LocalDate, List<LandCountDto>> getLandTimeSlotsByMonth(int year, int month);
}
    