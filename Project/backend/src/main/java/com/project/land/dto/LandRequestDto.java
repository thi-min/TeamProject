package com.project.land.dto;

import java.time.LocalDate;

import com.project.land.entity.LandType;
import com.project.reserve.dto.ReserveRequestDto;
import lombok.*;

@Getter
@Setter
@Builder
public class LandRequestDto {
    private ReserveRequestDto reserveDto;
    
    private LocalDate landDate;	//놀이터 예약일
    private Long timeSlotId; // 선택된 타임슬롯의 ID
    private LandType landType;	//놀이터유형(중,소형견/ 대형견)
    private int animalNumber;	//반려견 수
}