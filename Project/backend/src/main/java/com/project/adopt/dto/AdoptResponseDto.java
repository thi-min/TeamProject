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
    private Long adoptNum; //입양 번호
    private Long memberNum; // 회원번호 - service에서 memberentity를 사용
    private String memberName; //회원 이름 - service에서 memberentity를 사용
    private Long animalId;//동물 id - service에서 animalentity를 사용
    private String animalName; //동물 이름 - service에서 animalentity를 사용
    private LocalDate vistDt; //방문예정일
    private LocalDateTime consultDt;// 상담날짜
    private String adoptTitle;//제목
    private String adoptContent;//내용
    private AdoptState adoptState;//입양 진행상태
}