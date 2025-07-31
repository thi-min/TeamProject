package com.project.land.entity;

import java.time.LocalDate;

import com.project.common.entity.TimeSlot;
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
    
    @Column(name = "land_date")
    private LocalDate landDate;	//예약일
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id", nullable = false)
    private TimeSlot timeSlot; // 놀이터 예약 시간 슬롯
    
    @Enumerated(EnumType.STRING)
    @Column(name = "land_type")
    private LandType landType;	//놀이터 타입(반려견 타입)
    
    @Column(name = "animal_number") 
    private int animalNumber;	//반려견 수

    @Column(name = "pay_number") 
    private int payNumber;		// 결제금액

}