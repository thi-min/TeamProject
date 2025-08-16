package com.project.adopt.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.adopt.entity.AdoptState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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