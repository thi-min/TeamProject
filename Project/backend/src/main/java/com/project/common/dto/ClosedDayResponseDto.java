package com.project.common.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClosedDayResponseDto {
    private LocalDate closedDate;
    private String reason;
    private Boolean isClosed;
    
}
//달력에 휴무일 출력용