package com.project.fund.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.chat.entity.CheckState;
import com.project.fund.entity.FundType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 요청 DTO (생성/수정)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundRequestDto {

    // member id (member_num)
    @NotNull(message = "memberId is required")
    private Long memberId;

    @NotBlank(message = "fundSponsor is required")
    private String fundSponsor;

    private String fundPhone;

    private LocalDate fundBirth;

    @NotNull(message = "fundType is required")
    private FundType fundType;

    private BigDecimal fundMoney;

    // 생략 가능: fundTime는 서버에서 세팅 (create)
    private LocalDateTime fundTime;

    private String fundItem;

    private String fundNote;

    private String fundBank;

    private String fundAccountNum;

    private String fundDepositor;

    private LocalDate fundDrawlDate;

    private CheckState fundCheck;
}