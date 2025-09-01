package com.project.fund.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fund")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fund_id")
    private Long fundId;//후원번호

    @ManyToOne
    @JoinColumn(name = "member_num")
    private MemberEntity member;//회원번호

    @Column(name = "fund_sponsor", nullable = false)
    private String fundSponsor;//후원자명

    @Column(name = "fund_phone")
    private String fundPhone;//연락처

    @Column(name = "fund_birth")
    private LocalDate fundBirth;//생일

    @Enumerated(EnumType.STRING)
    @Column(name = "fund_type", length = 10)
    private FundType fundType;//후원종류

    @Column(name = "fund_money", precision = 15, scale = 2)
    private BigDecimal fundMoney;//후원금액

    @Column(name = "fund_time", nullable = false)
    private LocalDateTime fundTime;//후원 일시

    @Column(name = "fund_item")
    private String fundItem;//후원 물품

    @Column(name = "fund_note")
    private String fundNote;//비고

    @Column(name = "fund_bank")
    private String fundBank;//은행

    @Column(name = "fund_accountnum")
    private String fundAccountNum;//계좌

    @Column(name = "fund_depositor")
    private String fundDepositor;//예금주 명

    @Column(name = "fund_drawldate")
    private LocalDate fundDrawlDate;//출금일

    @Enumerated(EnumType.STRING)
    @Column(name = "fund_check", length = 1)
    private FundCheck fundCheck;//확인 상태
}