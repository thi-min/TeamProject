package com.project.land.dto;


import java.time.LocalDate;

import com.project.land.entity.LandType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandCountDto {
    private LocalDate landDate;  // 날짜
    private String label;     // 시간대 (예: "09:00 ~ 11:00")
    private LandType landType;  	//소형견 놀이터/ 대형견 놀이터
    private Integer reservedCount;   // 현재 예약된 마릿수
    private int capacity;        // 최대 수용 정원 (예: 30)
}