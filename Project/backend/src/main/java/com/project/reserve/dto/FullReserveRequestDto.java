package com.project.reserve.dto;

import com.project.land.dto.LandRequestDto;
import com.project.volunteer.dto.VolunteerRequestDto;

import lombok.*;

@Getter
@Setter
public class FullReserveRequestDto {
    private ReserveRequestDto reserveDto;
    private LandRequestDto landDto;
    private VolunteerRequestDto volunteerDto;
}