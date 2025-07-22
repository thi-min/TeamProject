package com.project.land.service;

import com.project.land.dto.LandDetailDto;

public interface LandService {
    LandDetailDto getLandDetailByReserveCode(Long reserveCode); //예약코드에 따라 상세보기페이지
}