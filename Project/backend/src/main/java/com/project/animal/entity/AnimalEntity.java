package com.project.animal.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "animal")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_id")
    private Long animalId;

    @Column(name = "animal_name")
    private String animalName;

    @Column(name = "animal_breed")
    private String animalBreed;

    @Enumerated(EnumType.STRING)
    @Column(name = "animal_sex", length = 10)
    private AnimalSex animalSex;

    @Column(name = "animal_date")
    private LocalDate animalDate; // 입소일

    @Column(name = "animal_content", columnDefinition = "TEXT")
    private String animalContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "animal_state", length = 10)
    private AnimalState animalState;

    @Column(name = "adopt_date")
    private LocalDate adoptDate; // 입양 날짜(문서에 adopt_state로 표기된 것의 의미가 '입양 날짜'로 보일 때)

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AnimalFileEntity> files;
}