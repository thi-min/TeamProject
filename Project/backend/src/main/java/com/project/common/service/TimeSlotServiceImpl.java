package com.project.common.service;

import com.project.common.dto.TimeSlotDto;
import com.project.common.entity.TimeSlot;
import com.project.common.entity.TimeType;
import com.project.common.repository.TimeSlotRepository;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.repository.ReserveRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final ReserveRepository reserveRepository;
    
    // 유효성 검사
    private void validateTimeSlotDto(TimeSlotDto dto) {
    	if (dto == null) {
            throw new IllegalArgumentException("시간대 정보가 누락되었습니다.");
        }
        if (dto.getStartTime().compareTo(dto.getEndTime()) >= 0) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 이전이어야 합니다.");
        }
        if (dto.getCapacity() < 1 || dto.getCapacity() > 30) {
            throw new IllegalArgumentException("정원은 1명 이상 30명 이하이어야 합니다.");
        }
    }
    
    // 타입별 시간대 조회
    @Override
    @Transactional(readOnly = true)
    public List<TimeSlotDto> getTimeSlotsByType(TimeType timeType) {
    	LocalDate today = LocalDate.now();
    	
        return timeSlotRepository
                .findByTimeTypeOrderByStartTimeAsc(timeType)
                .stream()
                .map(slot -> {
                	 boolean hasFutureReserve = false;
                	 List<ReserveState> activeStates = List.of(ReserveState.ING, ReserveState.DONE);
                	 
                	 if (timeType == TimeType.LAND) {
                		    hasFutureReserve = reserveRepository
                		        .existsByLandDetail_TimeSlot_IdAndLandDetail_LandDateAfterAndReserveStateIn(
                		            slot.getId(), today, activeStates
                		        );
                		} else if (timeType == TimeType.VOL) {
                		    hasFutureReserve = reserveRepository
                		        .existsByVolunteerDetail_TimeSlot_IdAndVolunteerDetail_VolDateAfterAndReserveStateIn(
                		            slot.getId(), today, activeStates
                		        );
                		}
                     TimeSlotDto dto = TimeSlotDto.fromEntity(slot);
                     dto.setHasFutureReserve(hasFutureReserve); // ✅ 예약 여부 주입
                     return dto;
                 })
                 .toList();
    }
    
    // 전체 시간대 조회
    public List<TimeSlotDto> getAllTimeSlots() {
        return timeSlotRepository.findAll()
                .stream()
                .map(TimeSlotDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    // 시간대 추가
    @Override
    @Transactional
    public void addTimeSlot(TimeSlotDto dto) {
        validateTimeSlotDto(dto);
        timeSlotRepository.save(dto.toEntity());
    }
    
    
    // 시간대 수정
    @Override
    @Transactional
    public void updateTimeSlot(Long timeSlotId, TimeSlotDto dto) {
        TimeSlot existing = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new IllegalArgumentException("해당 시간대가 존재하지 않습니다."));

        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        existing.setCapacity(dto.getCapacity());
        existing.setEnabled(dto.isEnabled());
        // label은 @PreUpdate로 자동 재생성됨
    }

    // 시간대 삭제
    @Override
    @Transactional
    public void deleteTimeSlot(Long timeSlotId) {
        if (!timeSlotRepository.existsById(timeSlotId)) {
            throw new IllegalArgumentException("존재하지 않는 시간대입니다.");
        }
        timeSlotRepository.deleteById(timeSlotId);
    }

    // 중복 여부 확인
    @Override
    @Transactional(readOnly = true)
    public boolean isDuplicateLabel(String label) {
        return timeSlotRepository.existsByLabel(label);
    }
    
}