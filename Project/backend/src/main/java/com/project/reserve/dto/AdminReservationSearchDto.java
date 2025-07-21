package com.project.reserve.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class AdminReservationSearchDto {
	private LocalDate startDate; // 검색 시작일
    private LocalDate endDate;   // 검색 종료일
    private String memberName;	//회원 이름
    private String reserveCode; //예약 코드
}