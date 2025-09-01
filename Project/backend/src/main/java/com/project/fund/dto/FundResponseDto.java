package com.project.fund.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.fund.entity.FundCheck;
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

    private Long fundId; // 후원번호

    private Long memberId; // 회원 번호

    private String fundSponsor; // 후원자명

    private String fundPhone;//연락처

    private LocalDate fundBirth;//생일

    private FundType fundType;//후원 종류

    private BigDecimal fundMoney;//후원 금액

    private LocalDateTime fundTime;//후원 일시

    private String fundItem;//후원물품

    private String fundNote;//비고

    private String fundBank;// 은행

    private String fundAccountNum;//계좌

    private String fundDepositor;//예금주 명

    private LocalDate fundDrawlDate;// 출금일

    private FundCheck fundCheck;//확인상태
}