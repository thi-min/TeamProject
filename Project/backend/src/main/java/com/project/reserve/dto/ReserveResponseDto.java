package com.project.reserve.dto;

import com.project.reserve.entity.ReserveState;
import com.project.member.Member;
import com.project.reserve.entity.Reserve;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveResponseDto {

	private Long memberNum;
    private Long reserveCode;
    private LocalDate reserveDate;
    private int reserveType;
    private ReserveState reserveState;
    private int reserveNumber;
    private LocalDate closedDate;
    
    //entity -> dto 변환 (서버가 응답한걸 사용자에게 넘기는 과정 ex)예약코드)
    public static ReserveResponseDto from(Reserve reserve) {
        Member member = reserve.getMember();
        return ReserveResponseDto.builder()
                .reserveCode(reserve.getReserveCode())
                .memberNum(member != null ? member.getMemberNum() : null)
                .reserveDate(reserve.getReserveDate())
                .reserveType(reserve.getReserveType())
                .reserveState(reserve.getReserveState())
                .reserveNumber(reserve.getReserveNumber())
                .closedDate(reserve.getClosedDate())
                .build();
    }
}