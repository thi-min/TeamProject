package com.project.common.service;

import java.util.List;

import com.project.common.dto.TimeSlotDto;

public interface TimeSlotService {
	
    // 관리자 - 시간대 추가
    void addTimeSlot(TimeSlotDto dto);

    // 관리자 - 시간대 수정
    void updateTimeSlot(Long id, TimeSlotDto dto);

    // 시간대 삭제
    void deleteTimeSlot(Long id);

    // 중복 여부 확인 (label 기준만)
    boolean isDuplicateLabel(String label);
    
    // 놀이터용 시간대 
    List<TimeSlotDto> getLandTimeSlots();
    
    // 봉사용 시간대
    List<TimeSlotDto> getVolunteerTimeSlots();  
    
    
}