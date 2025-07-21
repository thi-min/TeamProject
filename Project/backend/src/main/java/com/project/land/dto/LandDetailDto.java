package com.project.land.dto;

import com.project.land.entity.LandType;
import com.project.reserve.entity.ReserveState;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandDetailDto {

    // 기본 예약 정보
    private Long reserveCode;
    private String memberName;		// 신청자명
    private String phone;			// 연락처
    private ReserveState reserveState;
    private String reserveDate;	// 예: 2025/08/10
    private String timeSlot;		// 예: 11:00 ~ 13:00
    private String applyDate;      // 예: 2025/07/22 (신청일자, 필요시)
    private String note;

    // 놀이터 상세 정보
    private LandType landType;        // 놀이터유형(중,소형견/대형견)
    private int dogCount;          // 반려견 수
    private int peopleCount;       // 사육자 수

    // 결제 정보
    private int basePrice;
    private int additionalPrice;
    private int totalPrice;
    private String basePriceDetail;   // 예: "중형견 x 1"
    private String extraPriceDetail;  // 예: "추가 인원 x 2"
}