package com.project.reserv;

import com.project.entity.common.ReservState;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservRequestDto {

    private Long memberNum;        // 회원 ID (외래키)
    private LocalDate reservDate;
    private int reservType;
    private ReservState reservState;
    private int reservNumber;
    private LocalDate closedDate;
}