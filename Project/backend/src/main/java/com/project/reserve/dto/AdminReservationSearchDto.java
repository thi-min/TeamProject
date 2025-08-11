package com.project.reserve.dto;

import java.time.LocalDate;

import com.project.reserve.entity.ReserveState;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminReservationSearchDto {
	private LocalDate startDate; // 검색 시작일
    private LocalDate endDate;   // 검색 종료일
    private String memberName;	//회원 이름
    private Long reserveCode; //예약 코드
    private ReserveState reserveState; //예약상태
}
//관리자 검색필터용