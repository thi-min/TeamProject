package com.project.reserve.dto;

import com.project.common.ReservState;
import com.project.member.Member;
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
    private LocalDate reservDate;
    private int reservType;
    private ReservState reservState;
    private int reservNumber;
    private LocalDate closedDate;
    
    //dto -> entity 변환 (사용자가 예약정보 작성한걸 넘기는 과정)
    public Reserve toEntity(Member member) {
        return Reserve.builder()
                .member(member)
                .reserveDate(reservDate)
                .reserveType(reservType)
                .reserveState(ReservState.ING) // 예약 생성 시 기본 상태(예약 처리중)
                .reserveNumber(reservNumber)
                .closedDate(closedDate)
                .build();
    }
}