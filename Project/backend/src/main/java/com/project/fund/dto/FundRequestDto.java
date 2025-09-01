package com.project.fund.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.fund.entity.FundCheck;
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
    private Long memberId; // 회원번호

    @NotBlank(message = "fundSponsor is required")
    private String fundSponsor; // 후원자명

    private String fundPhone; //연락처

    private LocalDate fundBirth;//생일

    @NotNull(message = "fundType is required")
    private FundType fundType;//후원 종류

    private BigDecimal fundMoney;// 후원 금액

    // 생략 가능: fundTime는 서버에서 세팅 (create)
    private LocalDateTime fundTime; // 후원일시

    private String fundItem; // 후원 물품

    private String fundNote; //비고

    private String fundBank; // 은행

    private String fundAccountNum; // 계좌

    private String fundDepositor; // 예금주 명

    private LocalDate fundDrawlDate; // 출금일

    private FundCheck fundCheck; // 확인상태
}