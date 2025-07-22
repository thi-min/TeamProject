package com.project.reserve.dto;

import com.project.reserve.entity.ReserveState;
import com.project.member.entity.MemberEntity;
import com.project.reserve.entity.Reserve;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveResponseDto {

	private Long reserveCode;
	private Long memberNum;
	
    private LocalDateTime applyDate;
    private LocalDate reserveDate;       //놀이터 or 봉사 예약일 Land.landDate, Volunteer.volDate
    private int reserveType;
    private String reserveTypeName;
    private ReserveState reserveState;
    
  //entity -> dto 변환 (서버가 응답한걸 사용자에게 넘기는 과정 ex)예약코드)
    public static ReserveResponseDto from(Reserve reserve) {
        LocalDate resolvedDate;

        // 예약 유형에 따라 날짜 결정
        if (reserve.getReserveType() == 1 && reserve.getLandDetail() != null) {
            // 놀이터 예약 → Land.reserveDate
            resolvedDate = reserve.getLandDetail().getLandDate();
        } else if (reserve.getReserveType() == 2 && reserve.getVolunteerDetail() != null) {
            // 봉사 예약 → Volunteer.schedule
            resolvedDate = reserve.getVolunteerDetail().getVolDate();
        } else {
            resolvedDate = null; // 예외 처리 또는 기본값
        }
        
        MemberEntity member = reserve.getMember();	// member객체를 따로 꺼내는 코드(membernum을 가져오기위해)        
        return ReserveResponseDto.builder()
                .reserveCode(reserve.getReserveCode())
                .memberNum(member != null ? member.getMemberNum() : null)
                .applyDate(reserve.getApplyDate())
                .reserveDate(resolvedDate)
                .reserveType(reserve.getReserveType())
                .reserveTypeName(getReserveTypeName(reserve.getReserveType()))
                .reserveState(reserve.getReserveState())
                .build();
    }
    public static String getReserveTypeName(int type) {  //public: 모든클래스에서 접근가능 private:같은 클래스내에서만 접근가능
        return switch (type) {
            case 1 -> "놀이터 예약";
            case 2 -> "봉사 예약";
            default -> "알 수 없는 예약유형";
        };
    }
}
//사용자용 예약목록조회용 dto