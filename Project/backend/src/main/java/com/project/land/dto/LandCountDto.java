package com.project.land.dto;

import com.project.land.entity.LandType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandCountDto {
    private Long timeSlotId;         // ✅ 시간대 ID (TimeSlot.id)
    private String label;            // 예: "09:00 ~ 11:00"
    private LandType landType;       // 소형견 or 대형견
    private int reservedCount;       // 현재 예약된 반려견 수
    private int capacity;            // 최대 수용 정원
    
    public LandCountDto(Long timeSlotId, String label, LandType landType, Long reservedCount, Integer capacity) {
        this.timeSlotId = timeSlotId;
        this.label = label;
        this.landType = landType;
        this.reservedCount = reservedCount != null ? reservedCount.intValue() : 0;
        this.capacity = capacity != null ? capacity : 0;
    }
}