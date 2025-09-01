package com.project.adopt.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.animal.entity.AnimalEntity;
import com.project.member.entity.MemberEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "adopt")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adopt_num")
    private Long adoptNum; //입양번호

    @ManyToOne
    @JoinColumn(name = "member_num", nullable = false)
    private MemberEntity member;//회원번호 

    @ManyToOne
    @JoinColumn(name = "animal_id", nullable = false)
    private AnimalEntity animal;//동물ID

    @Column(name = "vist_dt")
    private LocalDate vistDt; //방문 예정일

    @Enumerated(EnumType.STRING)
    @Column(name = "adopt_state", length = 10)
    private AdoptState adoptState; //입양상태

    @Column(name = "adopt_title")
    private String adoptTitle;//제목

    @Column(name = "adopt_content", columnDefinition = "TEXT")
    private String adoptContent;//상담내용

    @Column(name = "consult_dt")
    private LocalDateTime consultDt; // 상담 날짜/시간
}