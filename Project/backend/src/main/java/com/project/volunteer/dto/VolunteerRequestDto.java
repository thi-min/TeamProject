package com.project.volunteer.dto;

import java.time.LocalDate;

import com.project.land.dto.LandRequestDto;
import com.project.reserve.dto.ReserveRequestDto;
import lombok.*;

@Getter
@Setter
@Builder
public class VolunteerRequestDto {
    private LocalDate volDate;	//일정
    private Long timeSlotId; // 선택된 타임슬롯의 ID
}