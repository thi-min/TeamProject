package com.project.volunteer.dto;

import java.time.LocalDate;

import com.project.reserve.dto.ReserveRequestDto;

public class VolunteerRequestDto {
	private Long memberNum;

    private LocalDate schedule;	//일정
    private String volTime;		//봉사시간
}