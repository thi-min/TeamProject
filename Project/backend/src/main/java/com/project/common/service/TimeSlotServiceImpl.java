package com.project.common.service;

import com.project.common.dto.TimeSlotDto;
import com.project.common.entity.TimeSlot;
import com.project.common.repository.TimeSlotRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;


    // 시간대 추가
    @Override
    @Transactional
    public void addTimeSlot(TimeSlotDto dto) {
        TimeSlot timeSlot = dto.toEntity(); // label은 엔티티 @PrePersist에서 자동 생성됨
        timeSlotRepository.save(timeSlot);
    }


    @Override
    @Transactional
    public void updateTimeSlot(Long id, TimeSlotDto dto) {
        TimeSlot existing = timeSlotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 시간대가 존재하지 않습니다."));

        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        existing.setEnabled(dto.isEnabled());
        // label은 @PreUpdate로 자동 재생성됨
    }

    // 시간대 삭제
    @Override
    @Transactional
    public void deleteTimeSlot(Long id) {
        if (!timeSlotRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 시간대입니다.");
        }
        timeSlotRepository.deleteById(id);
    }

    // 중복 여부 확인
    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicateLabel(String label) {
        return timeSlotRepository.existsByLabel(label);
    }
}