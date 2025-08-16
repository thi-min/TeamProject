package com.project.animal.dto;

import java.time.LocalDate;
import java.util.Set;

import com.project.animal.entity.AnimalSex;
import com.project.animal.entity.AnimalState;

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
public class AnimalResponseDto {
    private Long animalId;
    private String animalName;
    private String animalBreed;
    private AnimalSex animalSex;
    private AnimalState animalState;
    private LocalDate animalDate;
    private LocalDate adoptDate;
    private String animalContent;
    private Set<Long> fileIds;
}