package com.project.land.service;

import com.project.land.dto.LandDetailDto;
import com.project.land.dto.LandRequestDto;
import com.project.reserve.entity.Reserve;

public interface LandService {
    LandDetailDto getLandDetailByReserveCode(Long reserveCode); //예약코드에 따라 상세보기페이지
    
    void createLand(Reserve reserve, LandRequestDto landDto); //예약 생성시 land정보와 reserve정보 합칠때 사용
}