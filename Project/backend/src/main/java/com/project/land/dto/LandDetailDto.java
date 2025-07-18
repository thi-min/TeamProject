package com.project.land.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandDetailDto {

    // 신청자 정보
    private String memberName;
    private String contact;

    // 예약 정보
    private String dogType;        // 소형견 / 중형견
    private int dogCount;          // 반려견 수
    private int peopleCount;       // 사육자 수
    private String timeSlot;       // 예약 시간대 (ex: 11:00 ~ 13:00)

    // 결제 정보
    private int basePrice;         // 기본 금액 (ex: 2000)
    private int additionalPrice;   // 추가 금액 (ex: 2000)
    private int totalPrice;        // 총 금액 (ex: 4000)

}

