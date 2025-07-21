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
	private String memberName;     // 신청자명
    private String contact;        // 연락처


    // 예약 정보
    private ReserveState reserveState;   // 예약 상태 (ex: 신청중, 승인됨 등)
    private int peopleCount;       // 인원 수
    private String reserveDate;    // 예약일자
    private String timeSlot;       // 예약 시간
    private String note;           // 비고



}
