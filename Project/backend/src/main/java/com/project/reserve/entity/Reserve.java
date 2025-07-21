package com.project.reserve.entity;

import jakarta.persistence.*;
import lombok.*;

import com.project.land.entity.Land;
import com.project.member.entity.MemberEntity;
import com.project.reserve.entity.ReserveState;
import com.project.volunteer.entity.Volunteer;

import java.time.LocalDate;
import java.util.Date;
import java.time.LocalDateTime;

@Entity
@Table(name = "Reserve")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserve {

    @Id
    @Column(name = "reserve_code", nullable = false)
    private Long reserveCode;
    
    @OneToOne(mappedBy = "reserve", cascade = CascadeType.ALL) //land엔티티의 rserve필드가 외래키 관리
    private Land landDetail;

    @OneToOne(mappedBy = "reserve", cascade = CascadeType.ALL)
    private Volunteer volunteerDetail;

    @Column(name = "reserve_date")
    private LocalDate reserveDate;
    
    @Column(name = "apply_date")
    private LocalDateTime applyDate;
    
    @Column(name = "time_slot")
    private String timeSlot;
    
    @Column(name = "reserve_type")
    private Integer reserveType;

    @Enumerated(EnumType.STRING)
    @Column(name = "reserve_state" )
    private ReserveState reserveState;

    @Column(name = "reserve_number")
    private Integer reserveNumber;

    // 회원번호 (외래키)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num", nullable = false)
    private MemberEntity member; // Member 엔티티와 연결

}