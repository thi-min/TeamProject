package com.project.reserve.dto;

import com.project.reserve.entity.ReserveState;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
//상태변경(승인,거절,취소)
public class AdminReservationUpdateDto {
	//어떤 예약인지 어떤 상태로 바꿀것인지에 대해서 반드시 필요하기 떄문에
	//두 값이 없으면 로직 자체가 실행이 안되서 유효성 검사를 하도록 설정함(NotNull)
    @NotNull
    private Long reserveCode;      // 예약 식별자
    @NotNull
    private ReserveState reserveState;  // 변경할 상태 (예: "DONE", "REJ", "CANCEL")
}
//예약상태 변경하는 dto