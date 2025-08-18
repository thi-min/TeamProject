package com.project.fund.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.chat.entity.CheckState;
import com.project.fund.entity.FundType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundResponseDto {

    private Long fundId;

    private Long memberId;

    private String fundSponsor;

    private String fundPhone;

    private LocalDate fundBirth;

    private FundType fundType;

    private BigDecimal fundMoney;

    private LocalDateTime fundTime;

    private String fundItem;

    private String fundNote;

    private String fundBank;

    private String fundAccountNum;

    private String fundDepositor;

    private LocalDate fundDrawlDate;

    private CheckState fundCheck;
}