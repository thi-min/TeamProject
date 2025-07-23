package com.project.common.repository;

import com.project.common.entity.ClosedDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ClosedDayRepository extends JpaRepository<ClosedDay, LocalDate> {
	
	//날짜로 휴무일 조회
    Optional<ClosedDay> findByClosedDate(LocalDate closedDate);
    
    //해당 날짜가 존재하는지 확인
    boolean existsByClosedDate(LocalDate closedDate);
    
    //특정 날짜 삭제
    void deleteByClosedDate(LocalDate closedDate);
}