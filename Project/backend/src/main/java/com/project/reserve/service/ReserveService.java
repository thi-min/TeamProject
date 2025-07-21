package com.project.reserve.service;

import com.project.reserve.dto.AdminReservationListDto;
import com.project.reserve.dto.ReserveRequestDto;
import com.project.reserve.dto.ReserveResponseDto;
import com.project.reserve.entity.ReserveState;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ReserveService {

     Long createReserve(ReserveRequestDto requestDto); //사용자 예약생성

	 List<ReserveResponseDto> getReservesByMember(Long memberNum); 

	 ReserveResponseDto getReserveByCode(Long reserveCode); //공용

	 List<ReserveResponseDto> getReservesByDate(LocalDate date);

	 List<ReserveResponseDto> getReservesByType(int type);

	 void updateReserveState(Long reserveCode, ReserveState newState); //관리자

	 void memberCancelReserve(Long reserveCode, Long memberNum); //사용자
	 
	 List<AdminReservationListDto> getAllReservationsForAdmin(); //관리자용 조회
	 
	 List<AdminReservationListDto> searchReservationsForAdmin(String memberName, LocalDate startDate, LocalDate endDate); //관리자용 검색
}