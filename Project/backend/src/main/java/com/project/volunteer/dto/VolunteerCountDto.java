package com.project.volunteer.dto;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@Builder
public class VolunteerCountDto {
    private LocalDate volDate;	//날짜
    private String label;		// 시간대 (예: "09:00 ~ 12:00")
    private int reservedCount;
    private int capacity; // 고정값 예: 10명
}