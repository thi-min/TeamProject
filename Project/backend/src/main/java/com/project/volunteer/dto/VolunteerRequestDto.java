package com.project.volunteer.dto;

import java.time.LocalDate;

import com.project.reserve.dto.ReserveRequestDto;

public class VolunteerRequestDto {
	private Long memberNum;
    private int reserveNumber; //인원수
    private String note;		//비고

    private LocalDate schedule;	//일정
    private Integer volTime;	//봉사시간
}