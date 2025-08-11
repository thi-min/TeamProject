package com.project.adopt.dto;

import com.project.common.enums.AdoptState;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptResponseDto {
    private Long adoptNum;
    private Long memberNum;
    private String memberName;
    private Long animalId;
    private String animalName;
    private LocalDate vistDt;
    private LocalDateTime consultDt;
    private String adoptTitle;
    private String adoptContent;
    private AdoptState adoptState;
}