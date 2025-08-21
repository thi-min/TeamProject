package com.project.animal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "animal_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_file_id")
    private Long animalFileId; //동물 파일 번호

    @ManyToOne
    @JoinColumn(name = "animal_id", nullable = false)
    private AnimalEntity animal; //동물 번호

    @Column(name = "file_num")
    private Long fileNum; // 실제 파일 엔티티가 있다면 ManyToOne으로 대체 가능
}
