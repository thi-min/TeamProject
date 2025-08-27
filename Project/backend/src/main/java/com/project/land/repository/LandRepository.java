package com.project.land.repository;

import com.project.common.entity.TimeSlot;
import com.project.land.dto.LandCountDto;
import com.project.land.entity.Land;
import com.project.land.entity.LandType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LandRepository extends JpaRepository<Land, Long> {

    // 1:1 매핑된 Reserve 기준으로 조회
    Optional<Land> findByReserveCode(Long reserveCode);

    // 필요시 놀이터 타입별 조회
    List<Land> findByLandType(LandType landType);
    
    //int형 예약 마리수를 반환
    @Query("SELECT SUM(l.animalNumber) FROM Land l WHERE l.landDate = :landDate AND l.timeSlot = :timeSlot AND l.landType = :landType")
    Integer countByDateAndTimeAndType(@Param("landDate") LocalDate landDate,
                                      @Param("timeSlot") TimeSlot timeSlot,
                                      @Param("landType") LandType landType);
    
    //timeslotid가 놀이터예약에 사용된적있는지 확인
    boolean existsByTimeSlot_Id(Long timeSlotId);
    
    //프론트단에서 예약된 반려견 수 현황 확인
    @Query("""
    	    SELECT new com.project.land.dto.LandCountDto(
    	        ts.id,
    	        ts.label,
    	        :landType,
    	        COALESCE(SUM(
    	            CASE WHEN r.reserveState IN (
    	                com.project.reserve.entity.ReserveState.ING,
    	                com.project.reserve.entity.ReserveState.DONE
    	            )
    	            THEN l.animalNumber ELSE 0 END
    	        ), 0),
    	        ts.capacity,
    	        :date
    	    )
    	    FROM TimeSlot ts
    	    LEFT JOIN Land l 
    	        ON ts.id = l.timeSlot.id 
    	       AND l.landDate = :date 
    	       AND l.landType = :landType
    	    LEFT JOIN Reserve r 
    	        ON l.reserve.reserveCode = r.reserveCode
    	    WHERE ts.timeType = com.project.common.entity.TimeType.LAND
    	    GROUP BY ts.id, ts.label, ts.capacity, l.landType
    	    ORDER BY ts.startTime ASC
    	""")
    	List<LandCountDto> getLandCountInfo(
    	    @Param("date") LocalDate date,
    	    @Param("landType") LandType landType
    	);
    
}
