package com.project.entity.reserv;

import jakarta.persistence.*;
import lombok.*;
import com.project.entity.member.Member;
import com.project.entity.common.ReservState;
import java.time.LocalDate;

@Entity
@Table(name = "Reserv")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserv {

    @Id
    @Column(name = "reserv_code", nullable = false)
    private Long reservCode;

    @Column(name = "reserv_date")
    private LocalDate reservDate;

    @Column(name = "reserv_type")
    private Integer reservType;

    @Enumerated(EnumType.STRING)
    @Column(name = "reserv_state")
    private ReservState reservState;

    @Column(name = "reserv_number")
    private Integer reservNumber;

    @Column(name = "closed_date")
    private LocalDate closedDate;

    // 회원번호 (외래키)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num", nullable = false)
    private Member member; // Member 엔티티와 연결

}