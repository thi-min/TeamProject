package com.project.land.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Land")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Land {

    @Id
    @Column(name = "reserve_code", nullable = false)
    private Long reserveCode;

    @Column(name = "animal_number") 
    private int animalNumber;

    @Column(name = "pay_number") 
    private int payNumber;

}