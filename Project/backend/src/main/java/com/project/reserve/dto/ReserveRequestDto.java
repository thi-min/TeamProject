package com.project.reserve.dto;

import com.project.reserve.entity.ReserveState;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.project.member.entity.MemberEntity;
import com.project.reserve.entity.Reserve;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveRequestDto {

    private Long memberNum;        // 회원 ID (외래키)
    private LocalDate reserveDate;
    private int reserveType;
    private int reserveNumber;
    private String timeSlot;  // 선택적
    private String note;   
    
    //dto -> entity 변환 (사용자가 예약정보 작성한걸 넘기는 과정)
    public Reserve toEntity(MemberEntity member) {
        return Reserve.builder()
                .member(member)
                .reserveDate(reserveDate)
                .reserveType(reserveType)
                .reserveNumber(reserveNumber)
                .timeSlot(timeSlot)
                .applyDate(LocalDateTime.now())
                .build();
    }
}

//사용자가 예약 신청할때 사용하는 입력 dto (예약신청페이지)