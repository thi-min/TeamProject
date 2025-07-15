package com.project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Land")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandEntity {

    @Id
    @Column(name = "reserve_code") // 놀이터 ID
    private Long reserveCode;

    @Column(name = "animal_number", nullable = true) // 예: 마리수
    private int animalNumber;

    @Column(name = "pay_number", nullable = true) // 예: 결제금액
    private int payNumber;

}