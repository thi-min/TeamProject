package com.project.land.service;

import java.time.LocalDate;
import java.util.List;

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
    
    // 예약일과 시간대를 불러와서 반려견수 집계 + 예약 현황 정보 제공
    LandCountDto getLandCountInfo(LocalDate landDate, Long timeSlotId, LandType landType);
    
}
    