package com.project.animal.entity;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "animal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_id")
    private Long animalId; //동물 번호

    @Column(name = "animal_name")
    private String animalName; // 이름

    @Column(name = "animal_breed")
    private String animalBreed; //견종

    @Enumerated(EnumType.STRING)
    @Column(name = "animal_sex", length = 10)
    private AnimalSex animalSex; //성별

    @Column(name = "animal_date")
    private LocalDate animalDate; // 입소일

    @Column(name = "animal_content", columnDefinition = "TEXT")
    private String animalContent; //특이사항

    @Enumerated(EnumType.STRING)
    @Column(name = "animal_state", length = 10)
    private AnimalState animalState;//상태

    @Column(name = "adopt_date")
    private LocalDate adoptDate; // 입양 날짜(문서에 adopt_state로 표기된 것의 의미가 '입양 날짜'로 보일 때)

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AnimalFileEntity> files; //animalfileentity와 연결
}