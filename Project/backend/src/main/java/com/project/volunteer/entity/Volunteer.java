package com.project.volunteer.entity;

import com.project.common.entity.TimeSlot;
import com.project.reserve.entity.Reserve;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Volunteer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Volunteer {

    @Id
    @Column(name = "reserve_code", nullable = false)
    private Long reserveCode; // PK Reserve와 1:1 관계

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // reserve_code를 Reserve의 PK와 공유
    @JoinColumn(name = "reserve_code") //외래키 컬럼명
    private Reserve reserve;

    @Column(name = "vol_date")
    private LocalDate volDate; // 봉사 일정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id", nullable = false)
    private TimeSlot timeSlot; // 봉사 시간 슬롯
    
}