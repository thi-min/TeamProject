package com.project.animal.dto;

import com.project.common.enums.AnimalSex;
import com.project.common.enums.AnimalState;
import lombok.*;
import java.time.LocalDate;

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