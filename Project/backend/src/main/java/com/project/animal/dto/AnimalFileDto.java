package com.project.adopt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnimalFileDto {
    private Long animalFileId;
    private Long animalId;
    private Long fileNum;
}