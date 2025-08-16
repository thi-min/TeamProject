package com.project.fund.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.alarm.entity.CheckState;
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
@Table(name = "fund")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fund_id")
    private Long fundId;

    @ManyToOne
    @JoinColumn(name = "member_num")
    private MemberEntity member;

    @Column(name = "fund_sponsor", nullable = false)
    private String fundSponsor;

    @Column(name = "fund_phone")
    private String fundPhone;

    @Column(name = "fund_birth")
    private LocalDate fundBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "fund_type", length = 10)
    private FundType fundType;

    @Column(name = "fund_money", precision = 15, scale = 2)
    private BigDecimal fundMoney;

    @Column(name = "fund_time", nullable = false)
    private LocalDateTime fundTime;

    @Column(name = "fund_item")
    private String fundItem;

    @Column(name = "fund_note")
    private String fundNote;

    @Column(name = "fund_bank")
    private String fundBank;

    @Column(name = "fund_accountnum")
    private String fundAccountNum;

    @Column(name = "fund_depositor")
    private String fundDepositor;

    @Column(name = "fund_drawldate")
    private LocalDate fundDrawlDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "fund_check", length = 1)
    private CheckState fundCheck;
}