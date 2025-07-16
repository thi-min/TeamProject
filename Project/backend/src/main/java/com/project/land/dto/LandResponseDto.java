package com.project.land.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandResponseDto {

    private Long reserveCode;
    private int animalNumber;
    private int payNumber;
}
