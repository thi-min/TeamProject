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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "land_type")
    private LandType landType;	//놀이터 타입(반려견 타입)
    
    @Column(name = "animal_number") 
    private int animalNumber;	//반려견 수

    @Column(name = "pay_number") 
    private int payNumber;

}