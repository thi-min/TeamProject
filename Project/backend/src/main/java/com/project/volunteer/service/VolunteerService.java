package com.project.volunteer.service;

import com.project.reserve.entity.Reserve;
import com.project.volunteer.dto.VolunteerDetailDto;
import com.project.volunteer.dto.VolunteerRequestDto;

public interface VolunteerService {

    // reserveCode 기준으로 봉사 예약 상세 정보 조회
    VolunteerDetailDto getVolunteerDetailByReserveCode(Long reserveCode);
    
    //예약 생성시 volunteer정보와 reserv정보 합칠때 사용
    void createVolunteer(Reserve reserve, VolunteerRequestDto volunteerDto); 
    
}