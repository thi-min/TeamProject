package com.project.land.entity;

import com.project.reserve.entity.Reserve;

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
    
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // reserve_code를 Reserve의 PK와 공유
    @JoinColumn(name = "reserve_code")
    private Reserve reserve;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "land_type")
    private LandType landType;	//놀이터 타입(반려견 타입)
    
    @Column(name = "animal_number") 
    private int animalNumber;	//반려견 수

    @Column(name = "pay_number") 
    private int payNumber;

}