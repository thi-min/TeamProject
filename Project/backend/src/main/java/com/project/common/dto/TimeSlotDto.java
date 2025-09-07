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

    private Long timeSlotId;	//시간대id
    private String label; // 예: "09:00 ~ 11:00" — 출력용
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;	//정원
    private boolean enabled;	//활성여부
    private boolean hasFutureReserve; 	//예약존재여부
    private TimeType timeType;	//놀이터예약/봉사예약 구분

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