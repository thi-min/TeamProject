package com.project.reserve.dto;

import java.time.LocalDate;

import com.project.reserve.entity.Reserve;

import lombok.Data;

@Data
//관리자가 전체 예약 데이터 리스트형식으로 조회
public class AdminReservationListDto {
	private Long reserveCode;       // 예약 코드 (고유 식별자)
    private String memberName;      // 회원 이름 (조인해서 가져올 것)
    private String programName;     // 예약된 프로그램 이름
    private String reserveState;   // 상태 (예: DONE, REJ, ING)
    private LocalDate reserveDate; //예약일
    private int reserveType;	// 예약유형
    
    public static AdminReservationListDto from(Reserve reserve) {
        AdminReservationListDto dto = new AdminReservationListDto();
        dto.setReserveCode(reserve.getReserveCode());
        dto.setMemberName(reserve.getMember().getMemberName());
        dto.setProgramName(ReserveResponseDto.getReserveTypeName(reserve.getReserveType()));
        dto.setReserveState(reserve.getReserveState().name());

        // 예약일은 reserveType에 따라 분기
        if (reserve.getReserveType() == 1 && reserve.getLandDetail() != null) {
            dto.setReserveDate(reserve.getLandDetail().getLandDate()); // 놀이터 예약
        } else if (reserve.getReserveType() == 2 && reserve.getVolunteerDetail() != null) {
            dto.setReserveDate(reserve.getVolunteerDetail().getVolDate()); // 봉사 예약
        } else {
            dto.setReserveDate(null); // 잘못된 상태일 경우 null 처리
        }

        return dto;
    }
}