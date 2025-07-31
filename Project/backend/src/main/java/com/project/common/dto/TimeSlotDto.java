package com.project.common.dto;

import com.project.common.entity.TimeSlot;
import lombok.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotDto {

    private Long id;
    private String label; // 예: "09:00 ~ 11:00" — 출력용
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean enabled;

    // 출력용 시간 범위 포맷
    public String getDisplayTimeRange() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return startTime.format(formatter) + " ~ " + endTime.format(formatter);
    }

    // Entity → DTO 변환
    public static TimeSlotDto fromEntity(TimeSlot entity) {
        return TimeSlotDto.builder()
                .id(entity.getId())
                .label(entity.getLabel())  // 엔티티에 자동 생성된 label 사용
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .enabled(entity.isEnabled())
                .build();
    }

    // DTO → Entity 변환 (label은 생략, 엔티티에서 자동 생성됨)
    public TimeSlot toEntity() {
        return TimeSlot.builder()
                .id(id)
                .startTime(startTime)
                .endTime(endTime)
                .enabled(enabled)
                .build();
    }
}