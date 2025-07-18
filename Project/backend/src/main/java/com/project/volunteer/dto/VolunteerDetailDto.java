package com.project.volunteer.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerDetailDto {

    // 신청자 정보
    private String memberName;
    private String contact;

    // 예약 정보
    private LocalDate reserveDate;
    private String timeSlot;

    // 봉사 정보
    private LocalDate schedule;   // 봉사 날짜


}
