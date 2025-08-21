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
    private Long animalId; //동물 id
    private String animalName; //동물 이름
    private String animalBreed; //견종
    private AnimalSex animalSex; // 성별
    private AnimalState animalState; // 상태
    private LocalDate animalDate; // 입소일
    private LocalDate adoptDate; // 입소일 - service에서 adoptentity와 연결
    private String animalContent; // 특이사항
    private Set<Long> fileIds; // 첨부파일
}