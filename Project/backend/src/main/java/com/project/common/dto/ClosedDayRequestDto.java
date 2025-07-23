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
    private Boolean isClosed;
}