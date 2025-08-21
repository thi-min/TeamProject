package com.project.animal.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
    private Long animalId; // 동물 번호
    private String animalName; // 동물이름
    private String animalBreed; // 동물 견종
    private AnimalSex animalSex; // 동물 성별
    private LocalDate animalDate; // 입소일
    private String animalContent; // 특이사항
    private AnimalState animalState; // 상태 
    private LocalDate adoptDate;// 입양날짜
    
    private List<MultipartFile> files; // 여러 파일을 받을 경우
}