package com.project.volunteer.dto;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@Builder
public class VolunteerCountDto {
    private LocalDate volDate;
    private String volTime;
    private int reservedCount;
    private int capacity; // 고정값 예: 10명
}