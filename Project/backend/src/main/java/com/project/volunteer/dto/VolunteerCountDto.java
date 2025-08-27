package com.project.volunteer.dto;

import java.time.LocalDate;

import com.project.land.dto.LandCountDto;
import com.project.land.entity.LandType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerCountDto {
    private Long timeSlotId; 	// 타임슬롯 id
    private String label;		// 시간대 (예: "09:00 ~ 12:00")
    private int reservedCount;	// 예약된 사람수
    private int capacity; 		// 고정값 예: 10명
    private LocalDate volDate;	// 날짜 데이터
    
    public VolunteerCountDto(Long timeSlotId, String label, Long reservedCount, Integer capacity, LocalDate volDate) {
        this.timeSlotId = timeSlotId;
        this.label = label;
        this.reservedCount = reservedCount != null ? reservedCount.intValue() : 0;
        this.capacity = capacity != null ? capacity : 0;
        this.volDate = volDate;
    }
}