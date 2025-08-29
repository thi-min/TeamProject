package com.project.animal.dto;

import java.time.LocalDate;

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
public class AnimalRequestDto {
    private Long animalId;
    private String animalName;
    private String animalBreed;
    private AnimalSex animalSex;
    private LocalDate animalDate;
    private String animalContent;
    private AnimalState animalState;
    private LocalDate adoptDate;
}