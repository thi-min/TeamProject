package com.project.reserve.dto;

import com.project.reserve.entity.ReserveState;
import com.project.member.entity.MemberEntity;
import com.project.reserve.entity.Reserve;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveRequestDto {

    private Long memberNum;        // 회원 ID (외래키)
    private LocalDate reserveDate;
    private int reserveType;
    private ReserveState reserveState;
    private int reserveNumber;
    private LocalDate closedDate;
    
    //dto -> entity 변환 (사용자가 예약정보 작성한걸 넘기는 과정)
    public Reserve toEntity(Member member) {
        return Reserve.builder()
                .member(member)
                .reserveDate(reserveDate)
                .reserveType(reserveType)
                .reserveState(reserveState.ING) // 예약 생성 시 기본 상태(예약 처리중)
                .reserveNumber(reserveNumber)
                .closedDate(closedDate)
                .build();
    }
}