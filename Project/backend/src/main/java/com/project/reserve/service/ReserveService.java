package com.project.reserve.service;

import com.project.reserve.dto.AdminReservationListDto;
import com.project.reserve.dto.AdminReservationSearchDto;
import com.project.reserve.dto.ReserveDetailDto;
import com.project.reserve.dto.ReserveRequestDto;
import com.project.reserve.dto.ReserveResponseDto;
import com.project.reserve.entity.ReserveState;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ReserveService {

     Long createReserve(ReserveRequestDto requestDto); //사용자 - 예약생성

	 List<ReserveResponseDto> getReservesByMember(Long memberNum); //사용자 - 본인의 얘약 목록 조회(마이페이지)

	 ReserveDetailDto getMemberReserveByCode(Long reserveCode, Long memberNum); //사용자 상세보기
	 
	 ReserveDetailDto getAdminReserveByCode(Long reserveCode); //관리자 상세보기
	 
	 void updateReserveStateByAdmin(Long reserveCode, ReserveState newState); //관리자

	 void memberCancelReserve(Long reserveCode, Long memberNum); //사용자

	 List<ReserveResponseDto> getReservesByType(int type); //사용자 - 마이페이지에서 놀이터예약/봉사예약 탭

	 List<AdminReservationListDto> getAllReservationsForAdmin(); //관리자용 전체예약목록 조회
	 
	 List<AdminReservationListDto> searchReservationsForAdmin(AdminReservationSearchDto searchDto); //관리자용 검색(회원명, 예약코드, 기간. 상태)
}