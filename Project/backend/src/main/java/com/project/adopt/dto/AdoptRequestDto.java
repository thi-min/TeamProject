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
public class AdoptRequestDto {
    private Long adoptNum; //입양번호
    private Long memberNum;// 회원번호
    private Long animalId;// 동물id
    private LocalDate vistDt;// 방문 예정일
    private LocalDateTime consultDt;// 상담 날짜/시간
    private String adoptTitle;//제목
    private String adoptContent;//상담내용
    private AdoptState adoptState;//입양 진행상태
}