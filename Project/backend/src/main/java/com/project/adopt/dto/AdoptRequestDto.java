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
public class AdoptRequestDto {
    private Long adoptNum;
    private Long memberNum;
    private Long animalId;
    private LocalDate vistDt;
    private LocalDateTime consultDt;
    private String adoptTitle;
    private String adoptContent;
    private AdoptState adoptState;
}