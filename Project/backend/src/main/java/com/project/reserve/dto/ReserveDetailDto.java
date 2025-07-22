package com.project.reserve.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveDetailDto {

    // 예약 정보
    private Long reserveCode;
    private String memberName;
    private String phone;
    private String programName;
    private int dogCount;
    private int peopleCount;
    private LocalDate applyDate;
    private LocalDate reserveDate;
    private String reserveTime;  // 예: "11:00 ~ 13:00"
    private String note;

    // 결제 정보
    private int totalPrice;
    private int basePrice;
    private int extraPrice;
    private String basePriceDetail;   // 예: "중, 소형견 x 1마리"
    private String extraPriceDetail;  // 예: "인원추가 x 2명"

    // 예약 상태 등 추가 필드 필요시 여기에 계속 확장 가능
}
