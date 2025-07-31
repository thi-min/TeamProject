package com.project.land.repository;

import com.project.common.entity.TimeSlot;
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
}