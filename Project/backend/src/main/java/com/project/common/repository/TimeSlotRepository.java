package com.project.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.project.common.entity.TimeType; 
import com.project.common.entity.TimeSlot;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

	// 시간슬롯이 중복되는지 확인 (label 기준만)
    boolean existsByLabel(String label);

    // 시간순 정렬 (startTime 기준 추천)
    List<TimeSlot> findByTimeTypeOrderByStartTimeAsc(TimeType timeType);
    
    // 시간대 유효성
    boolean existsByIdAndTimeTypeAndEnabled(Long id, TimeType timeType, boolean enabled);
    
}