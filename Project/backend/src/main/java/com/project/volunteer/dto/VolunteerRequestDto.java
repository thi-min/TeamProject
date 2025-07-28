package com.project.volunteer.dto;

import java.time.LocalDate;

import com.project.land.dto.LandRequestDto;
import com.project.reserve.dto.ReserveRequestDto;
import lombok.*;

@Getter
@Setter
@Builder
public class VolunteerRequestDto {
	private Long memberNum;

    private LocalDate volDate;	//일정
    private String volTime;		//봉사시간
}