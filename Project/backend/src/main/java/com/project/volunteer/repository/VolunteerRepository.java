package com.project.volunteer.repository;

import com.project.common.entity.TimeSlot;
import com.project.volunteer.dto.VolunteerCountDto;
import com.project.volunteer.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    // 예약 코드로 봉사 정보 조회
    Optional<Volunteer> findByReserveCode(Long reserveCode);
    
    // 봉사시간에 따른 봉사인원 체크
    @Query("SELECT SUM(r.reserveNumber) FROM Volunteer v JOIN v.reserve r WHERE v.volDate = :volDate AND v.timeSlot = :timeSlot")
    Integer countByDateAndTimeSlot(@Param("volDate") LocalDate volDate, 
                                   @Param("timeSlot") TimeSlot timeSlot);
    
    // timeslotid가 봉사예약용으로 사용된적 있는지 확인
    boolean existsByTimeSlot_Id(Long timeSlotId);
    
    // 프론트단에서 예약된 신청자수 확인
    @Query("""
    	    SELECT new com.project.volunteer.dto.VolunteerCountDto(
    	        ts.id,
    	        ts.label,
    	        COALESCE(SUM(
    	            CASE WHEN r.reserveState IN (
    	                com.project.reserve.entity.ReserveState.ING,
    	                com.project.reserve.entity.ReserveState.DONE
    	            )
    	            THEN r.reserveNumber ELSE 0 END
    	        ), 0),
    	        ts.capacity,
    	        :date
    	    )
    	    FROM TimeSlot ts
    	    LEFT JOIN Volunteer v 
    	        ON ts.id = v.timeSlot.id 
    	       AND v.volDate = :date
    	    LEFT JOIN Reserve r 
    	        ON v.reserve.reserveCode = r.reserveCode
    	    WHERE ts.timeType = com.project.common.entity.TimeType.VOL
    	    GROUP BY ts.id, ts.label, ts.capacity
    	    ORDER BY ts.startTime ASC
    	    """)
    	List<VolunteerCountDto> getVolunteerCountInfo(@Param("date") LocalDate date);
}