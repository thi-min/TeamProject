package com.project.admin;

import java.time.LocalDateTime;
import lombok.Data;

@Data
//전체 예약 데이터 확인
public class AdminReservationListDto {
	private Long reserveCode;       // 예약 코드 (고유 식별자)
    private Long memberNum;         // 회원번호
    private String memberName;      // 회원 이름 (조인해서 가져올 것)
    private String programName;     // 예약된 프로그램 이름
    private LocalDateTime reserveDate; // 예약 일시
    private String reserveState;   // 상태 (예: DONE, REJ, ING)
    
    //검색 필터링
    //reserveCode
    //memberName
    
}
