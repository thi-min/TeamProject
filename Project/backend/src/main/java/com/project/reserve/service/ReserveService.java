package com.project.reserve.service;

import com.project.land.dto.LandDetailDto;
import com.project.reserve.dto.AdminReservationListDto;
import com.project.reserve.dto.AdminReservationSearchDto;
import com.project.reserve.dto.FullReserveRequestDto;
import com.project.reserve.dto.ReserveCompleteResponseDto;
import com.project.reserve.dto.ReserveRequestDto;
import com.project.reserve.dto.ReserveResponseDto;
import com.project.reserve.entity.ReserveState;
import com.project.volunteer.dto.VolunteerDetailDto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ReserveService {

	 ReserveCompleteResponseDto createReserve(FullReserveRequestDto fullRequestDto); //사용자 - 예약생성

	 List<ReserveResponseDto> getReservesByMember(Long memberNum); //사용자 - 본인의 예약 목록 조회(마이페이지)
	 
	 List<ReserveResponseDto> getReservesByMemberAndType(Long memberNum, int type); // 사용자 - 마이페이지에서 놀이터예약/봉사예약 탭
	 
	 LandDetailDto getMemberLandReserveDetail(Long reserveCode, Long memberNum); //사용자 - 놀이터예약 상세보기
	 
	 VolunteerDetailDto getMemberVolunteerReserveDetail(Long reserveCode, Long memberNum); //사용자 - 봉사예약 상세보기
	 
	 void memberCancelReserve(Long reserveCode, Long memberNum); //사용자 - 예약취소
	 
	 List<AdminReservationListDto> getAllReservationsForAdmin(); //관리자용 전체예약목록 조회
	 
	 List<AdminReservationListDto> getLandReservationsForAdmin(); //관리자용 놀이터예약 목록 (탭) 불러오기
	 
	 List<AdminReservationListDto> getVolunteerReservationsForAdmin();//관리자용 봉사예약 목록 (탭) 불러오기

	 List<AdminReservationListDto> searchLandReservationsForAdmin(AdminReservationSearchDto dto); // 관리자용 놀이터 검색

	 List<AdminReservationListDto> searchVolunteerReservationsForAdmin(AdminReservationSearchDto dto); // 관리자용 봉사 검색
	 
	 LandDetailDto getAdminLandReserveDetail(Long reserveCode); //관리자 - 놀이터 예약 상세보기
	 
	 VolunteerDetailDto getAdminVolunteerReserveDetail(Long reserveCode); //관리자 - 봉사 예약 상세보기
	 
	 void updateReserveStateByAdmin(Long reserveCode, ReserveState newState); //관리자 - 예약 상태 변경
	 
	 boolean existsLandDuplicate(Long memberNum, LocalDate date, Long timeSlotId); //사용자 - 놀이터예약 중복검사
	 
	 boolean existsVolunteerDuplicate(Long memberNum, LocalDate date, Long timeSlotId); //사용자 -봉사예약 중복검사

	 
	 
}