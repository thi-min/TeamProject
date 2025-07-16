package com.project.reserve.entity;

import jakarta.persistence.*;
import lombok.*;
import com.project.member.Member;
import com.project.reserve.entity.ReserveState;
import java.time.LocalDate;

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

    @Column(name = "reserve_date")
    private LocalDate reserveDate;

    @Column(name = "reserve_type")
    private Integer reserveType;

    @Enumerated(EnumType.STRING)
    @Column(name = "reserve_state")
    private ReserveState reserveState;

    @Column(name = "reserve_number")
    private Integer reserveNumber;

    @Column(name = "closed_date")
    private LocalDate closedDate;

    // 회원번호 (외래키)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num", nullable = false)
    private Member member; // Member 엔티티와 연결

}