package com.project.volunteer.dto;

import lombok.*;

import java.time.LocalDate;

import com.project.reserve.entity.ReserveState;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerDetailDto {

    // 신청자 정보
	private Long reserveCode;
	private String memberName;     // 신청자명
    private String phone;        // 연락처
    private LocalDate memberBirth; //생년월일


    // 예약 정보
    private ReserveState reserveState;   // 예약 상태 (ex: 신청중, 승인됨 등)
    private int reserveNumber;       // 인원 수
    private LocalDate volDate;       // 실제 봉사 활동이 있는 날짜 (센터 지정)
    private String volTime;       // 예약 시간
    private String note;           // 비고
      // 실제 봉사 활동이 있는 날짜 (센터 지정)
   	//봉사 시간


}
