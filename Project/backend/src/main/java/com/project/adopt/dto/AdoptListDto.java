package com.project.adopt.dto;

import com.project.common.enums.AdoptState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdoptListDto {
    private Long adoptNum;
    private Long memberNum;
    private String memberName;
    private Long animalId;
    private String adoptTitle;
    private LocalDateTime consultDt;
    private java.time.LocalDate vistDt;
    private AdoptState adoptState;
}