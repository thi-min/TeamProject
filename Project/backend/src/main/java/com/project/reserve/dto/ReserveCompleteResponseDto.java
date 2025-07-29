package com.project.reserve.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveCompleteResponseDto {
	private Long reserveCode;
    private String message; //  예: "놀이터 예약이 완료되었습니다."
}