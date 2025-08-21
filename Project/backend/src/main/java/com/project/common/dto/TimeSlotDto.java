package com.project.common.dto;

import com.project.common.entity.TimeSlot;
import com.project.common.entity.TimeType;

import lombok.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotDto {

    private Long timeSlotId;
    private String label; // 예: "09:00 ~ 11:00" — 출력용
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
    private boolean enabled;
    private boolean hasFutureReserve; 
    private TimeType timeType;

    // 출력용 시간 범위 포맷
    public String getDisplayTimeRange() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return startTime.format(formatter) + " ~ " + endTime.format(formatter);
    }

    // Entity → DTO 변환
    public static TimeSlotDto fromEntity(TimeSlot entity) {
        return TimeSlotDto.builder()
                .timeSlotId(entity.getId())
                .label(entity.getLabel())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .enabled(entity.isEnabled())
                .capacity(entity.getCapacity())
                .timeType(entity.getTimeType())
                .build();
    }


    // DTO → Entity 변환 (label은 생략, 엔티티에서 자동 생성됨)
    public TimeSlot toEntity() {
        return TimeSlot.builder()
        		.id(timeSlotId)
                .startTime(startTime)
                .endTime(endTime)
                .capacity(capacity)
                .timeType(timeType)        
                .enabled(enabled)
                .build();
    }
    
   
}