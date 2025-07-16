package com.project.reserv;

import com.project.common.ReservState;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservResponseDto {

	private Long memberNum;
    private Long reservCode;
    private LocalDate reservDate;
    private int reservType;
    private ReservState reservState;
    private int reservNumber;
    private LocalDate closedDate;
}