package com.project.land.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.land.entity.LandType;
import com.project.reserve.entity.ReserveState;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandDetailDto {

    // 신청자 정보 
    private Long reserveCode;
    private String memberName;		// 신청자명
    private String memberPhone;			// 연락처
    
    // 예약 정보
    private ReserveState reserveState;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    private LocalDate landDate;	// 예: 2025/08/10
    private String label;		// 예: 11:00 ~ 13:00
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime applyDate;      // 예: 2025/07/22 22:00:24 (신청일자)
    private String note;			//비고

    // 놀이터 상세 정보
    private LandType landType;        // 놀이터유형(중,소형견/대형견)
    private int animalNumber;          // 반려견 수
    private int reserveNumber;       // 사육자 수

    // 결제 정보
    private int basePrice;
    private int additionalPrice;
    private int totalPrice;
    private String basePriceDetail;   // 예: "중형견 x 1"
    private String extraPriceDetail;  // 예: "추가 인원 x 2"
}