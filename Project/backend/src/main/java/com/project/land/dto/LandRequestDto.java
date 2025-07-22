package com.project.land.dto;

import java.time.LocalDate;

import com.project.land.entity.LandType;
import com.project.reserve.dto.ReserveRequestDto;

public class LandRequestDto {
    private ReserveRequestDto reserveDto;
    
    private LocalDate reserveDate;
    private String reserveTime;
    private LandType landType;
    private int animalNumber;
    private int payNumber;
}