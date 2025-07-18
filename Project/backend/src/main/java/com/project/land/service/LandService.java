package com.project.land.service;

import com.project.land.dto.LandDetailDto;

public interface LandService {
    LandDetailDto getLandDetailByReserveCode(Long reserveCode);
}