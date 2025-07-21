package com.project.adopt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.project.member.entity.MemberEntity;

@Entity
@Table(name = "Adopt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Adopt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adopt_num", nullable = false)
    private Long adoptNum;

    // 외래키: 회원
    @ManyToOne(fetch = FetchType.LAZY) //다대일 관계
    @JoinColumn(name = "member_num", nullable = false)
    private MemberEntity member;

    @Column(name = "animal_id")
    private Long animalId;

    @Column(name = "consult_dt")
    private LocalDate consultDate;

    @Column(name = "vist_dt")
    private LocalDate visitDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "adopt_sts")
    private AdoptSts adoptSts;
}