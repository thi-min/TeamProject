package com.project.common.repository;

import com.project.common.entity.ClosedDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClosedDayRepository extends JpaRepository<ClosedDay, LocalDate> {
	
	//날짜로 휴무일 조회
    Optional<ClosedDay> findByClosedDate(LocalDate closedDate);
    
    //해당 날짜가 존재하는지 확인
    boolean existsByClosedDate(LocalDate closedDate);
    
    //달력ui 이용해서 이번달 달력 예약 불가일 회색 표시 + 관리자 휴무일목록 보고 편집
    List<ClosedDay> findByClosedDateBetween(LocalDate start, LocalDate end);
    
    //대량 등록시 중복 날짜만 선별
    @Query("SELECT c.closedDate FROM ClosedDay c WHERE c.closedDate IN :dates")
    List<LocalDate> findExistingDates(@Param("dates") List<LocalDate> dates);
    
    //특정 날짜 삭제
    void deleteByClosedDate(LocalDate closedDate);
}