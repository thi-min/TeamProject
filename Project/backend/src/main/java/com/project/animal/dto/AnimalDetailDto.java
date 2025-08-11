package com.project.adopt.dto;

import com.project.common.enums.AnimalSex;
import com.project.common.enums.AnimalState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnimalDetailDto {
    private Long animalId;
    private String animalName;
    private String animalBreed;
    private AnimalSex animalSex;
    private AnimalState animalState;
    private LocalDate animalDate;
    private LocalDate adoptDate;
    private String animalContent;
    private Set<Long> fileIds; // AnimalFileEntity.fileNum 또는 animalFileId 목록
}