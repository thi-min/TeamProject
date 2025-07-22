package com.project.land.dto;

import java.time.LocalDate;

import com.project.land.entity.LandType;
import com.project.reserve.dto.ReserveRequestDto;
import lombok.*;

@Getter
@Setter
public class LandRequestDto {
    private ReserveRequestDto reserveDto;
    
    private LocalDate landDate;	//놀이터 예약일
    private String landTime;	//예약시간 (타임슬롯)
    private LandType landType;	//놀이터유형(중,소형견/ 대형견)
    private int animalNumber;	//반려견 수
    private int payNumber;		//결제 금액 
}