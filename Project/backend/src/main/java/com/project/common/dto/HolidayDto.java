package com.project.common.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HolidayDto{
    
    private LocalDate date;     // ex. 2025-01-01 (locdate 파싱 결과)
    private String name;        // ex. "신정", "설날", "크리스마스"
    private String isHoliday;   // "Y" 또는 "N" (공식 공휴일 여부)
}