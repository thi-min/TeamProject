package com.project.animal.dto;

import com.project.common.enums.AnimalSex;
import com.project.common.enums.AnimalState;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

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