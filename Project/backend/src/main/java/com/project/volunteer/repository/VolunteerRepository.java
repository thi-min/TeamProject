package com.project.volunteer.repository;

import com.project.common.entity.TimeSlot;
import com.project.volunteer.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    // 예약 코드로 봉사 정보 조회
    Optional<Volunteer> findByReserveCode(Long reserveCode);
    
    //봉사시간에 따른 봉사인원 체크
    @Query("SELECT SUM(r.reserveNumber) FROM Volunteer v JOIN v.reserve r WHERE v.volDate = :volDate AND v.timeSlot = :timeSlot")
    Integer countByDateAndTimeSlot(@Param("volDate") LocalDate volDate, 
                                   @Param("timeSlot") TimeSlot timeSlot);
    
    // timeslotid가 봉사예약용으로 사용된적 있는지 확인
    boolean existsByTimeSlot_TimeSlotId(Long timeSlotId);
}