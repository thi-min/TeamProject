package com.project.animal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Animal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_id")
    private Long animalId;			//동물id

    @Column(name = "animal_name", length = 12) //강아지명
    private String animalName;

    @Column(name = "animal_breed", length = 12)	//견종
    private String animalBreed;

    @Column(name = "animal_sex")
    private String animalSex; // 예: "MALE", "FEMALE"

    @Column(name = "animal_date")
    private LocalDate animalDate; // 보호소 입소일

    @Column(name = "animal_content", length = 255)
    private String animalContent; // 특이사항 등 (예방주사)

    @Enumerated(EnumType.STRING)
    @Column(name = "animal_state")
    private AnimalState animalState;	//현재상태(입양)

    @Column(name = "adopt_date")
    private LocalDate adoptDate;	//입양날짜
}