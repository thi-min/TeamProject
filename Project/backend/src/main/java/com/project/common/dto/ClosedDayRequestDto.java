package com.project.common.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClosedDayRequestDto {
    private LocalDate closedDate;
    private String reason;
    private Boolean isClosed;
}
//휴무일 등록, 수정용