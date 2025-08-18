package com.project.common.service;

import java.util.List;

import com.project.common.dto.TimeSlotDto;
import com.project.common.entity.TimeType;

public interface TimeSlotService {
	
    // 관리자 - 시간대 추가
    void addTimeSlot(TimeSlotDto dto);

    // 관리자 - 시간대 수정
    void updateTimeSlot(Long timeSlotID, TimeSlotDto dto);

    // 시간대 삭제
    void deleteTimeSlot(Long timeSlotId);

    // 중복 여부 확인 (label 기준만)
    boolean isDuplicateLabel(String label);
    
    // 타입별 시간대 조회
    List<TimeSlotDto> getTimeSlotsByType(TimeType timeType);
    
    // 전체 시간대 조회
    List<TimeSlotDto> getAllTimeSlots();
    
}