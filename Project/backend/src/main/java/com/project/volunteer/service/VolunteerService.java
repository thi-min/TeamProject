package com.project.volunteer.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.project.common.entity.TimeSlot;
import com.project.reserve.entity.Reserve;
import com.project.volunteer.dto.VolunteerCountDto;
import com.project.volunteer.dto.VolunteerDetailDto;
import com.project.volunteer.dto.VolunteerRequestDto;

public interface VolunteerService {

    // reserveCode 기준으로 봉사 예약 상세 정보 조회
    VolunteerDetailDto getVolunteerDetailByReserveCode(Long reserveCode);
    
    //예약 생성시 volunteer정보와 reserv정보 합칠때 사용
    void createVolunteer(Reserve reserve, VolunteerRequestDto volunteerDto, TimeSlot timeSlot); 
    
    // 단일 시간대 봉사인원체크 (관리자용)
    VolunteerCountDto getVolunteerCountInfo(LocalDate volDate, Long timeSlotId);
    
    // 전체 시간대별 현황 조회 (사용자용)  
    List<VolunteerCountDto> getVolunteerTimeSlotsWithCount(LocalDate volDate); 
    List<VolunteerCountDto> getVolunteerTimeSlotsWithCount(LocalDate volDate, Long memberNum);
    
    // 월별 정원 체크(사용자용)
    Map<LocalDate, List<VolunteerCountDto>> getVolunteerTimeSlotsByMonth(int year, int month);
}