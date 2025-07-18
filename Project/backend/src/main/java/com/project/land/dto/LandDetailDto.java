package com.project.land.dto;

import com.project.reserve.entity.ReserveState;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandDetailDto {

    // 신청자 정보
    private String memberName;		//신청자명
    private String contact;			//연락처

    // 예약 정보
    private String dogType;        // 소형견,중형견 / 대형견
    private int dogCount;          // 반려견 수
    private int peopleCount;       // 사육자 수
    private ReserveState resesrveState;
    private String reserveDate;    // 예약일자 (예: 2025/08/10 형식으로 출력)
    private String timeSlot;       // 예약 시간대 (ex: 11:00 ~ 13:00)
    private String note;		   //비고
    
    // 결제 정보
    private int basePrice;         // 기본 금액 (ex: 2000)
    private int additionalPrice;   // 추가 금액 (ex: 2000)
    private int totalPrice;        // 총 금액 (ex: 4000)

}

