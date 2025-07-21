package com.project.volunteer.service;

import com.project.volunteer.dto.VolunteerDetailDto;

public interface VolunteerService {

    // reserveCode 기준으로 봉사 예약 상세 정보 조회
    VolunteerDetailDto getVolunteerDetailByReserveCode(Long reserveCode);
}