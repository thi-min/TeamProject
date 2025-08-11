package com.project.reserve.dto;

import com.project.land.dto.LandRequestDto;
import com.project.volunteer.dto.VolunteerRequestDto;

import lombok.*;

@Getter
@Setter
@Builder
public class FullReserveRequestDto {
    private ReserveRequestDto reserveDto;
    private LandRequestDto landDto;
    private VolunteerRequestDto volunteerDto;
}
//기본 예약 정보와 예약 유형별로 상제 정보를 합친 하나의 통합요청dto 