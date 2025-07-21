package com.project.land.dto;

import com.project.land.entity.LandType;
import com.project.reserve.dto.ReserveRequestDto;

public class LandRequestDto {
    private ReserveRequestDto reserveDto;

    private LandType landType;
    private int dogCount;
    private int payNumber;
}