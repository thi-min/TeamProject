package com.project.land.dto;


import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandCountDto {
    private LocalDate landDate;  // 날짜
    private String landTime;     // 시간대 (예: "09:00 ~ 11:00")
    private Integer reservedCount;   // 현재 예약된 마릿수
    private int capacity;        // 최대 수용 정원 (예: 30)
}