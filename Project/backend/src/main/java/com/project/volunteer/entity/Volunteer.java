package com.project.volunteer.entity;

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

    @Column(name = "vol_time")
    private String volTime; // 봉사 시간 (셀렉트박스 형식 선택지 3개)
    
}