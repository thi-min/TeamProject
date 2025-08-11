package com.project.animal.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "animal_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_file_id")
    private Long animalFileId; // PK

    @ManyToOne
    @JoinColumn(name = "animal_id", nullable = false)
    private AnimalEntity animal;

    @Column(name = "file_num")
    private Long fileNum; // 실제 파일 엔티티가 있다면 ManyToOne으로 대체 가능
}