package com.project.volunteer.service;

import com.project.volunteer.dto.VolunteerCountDto;
import com.project.volunteer.dto.VolunteerDetailDto;
import com.project.volunteer.dto.VolunteerRequestDto;
import com.project.volunteer.entity.Volunteer;
import com.project.volunteer.repository.VolunteerRepository;
import com.project.reserve.entity.Reserve;
import com.project.reserve.repository.ReserveRepository;
import com.project.member.entity.MemberEntity;
import com.project.common.entity.TimeSlot;
import com.project.common.entity.TimeType;
import com.project.common.repository.TimeSlotRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VolunteerServiceImpl implements VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final ReserveRepository reserveRepository;
    private final TimeSlotRepository timeSlotRepository;
    
    //봉사 상세내용 조회
    @Override
    @Transactional(readOnly = true)
    public VolunteerDetailDto getVolunteerDetailByReserveCode(Long reserveCode) {
        // 1. Volunteer 조회
        Volunteer volunteer = volunteerRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약에 대한 봉사 정보를 찾을 수 없습니다."));

        // 2. Reserve 조회
        Reserve reserve = volunteer.getReserve();

        // 3. 회원 정보 조회
        MemberEntity member = reserve.getMember();

        // 4. DTO 생성 및 반환
        return VolunteerDetailDto.builder()
                .reserveCode(reserve.getReserveCode())
                .memberName(member.getMemberName())
                .phone(member.getMemberPhone())
                .memberBirth(member.getMemberBirth())
                .reserveState(reserve.getReserveState())
                .volDate(volunteer.getVolDate())
                .label(volunteer.getTimeSlot().getLabel())
                .note(reserve.getNote())
                .reserveNumber(reserve.getReserveNumber())
                .build();
    }
    
    //봉사 예약 생성(기본 정보랑 봉사 상세 정보 합친 예약)
    @Override
    public void createVolunteer(Reserve reserve, VolunteerRequestDto volunteerDto, TimeSlot timeSlot) {
        Volunteer volunteer = Volunteer.builder()
                .reserve(reserve)
                .volDate(volunteerDto.getVolDate())
                .timeSlot(timeSlot)
                .build();
        
        reserve.setVolunteerDetail(volunteer);	//양방향 연결 
        
        volunteerRepository.save(volunteer);
    }
    
    // 관리자용 - 단일 시간대에 봉사 인원 체크
    @Override
    @Transactional(readOnly = true)
    public VolunteerCountDto getVolunteerCountInfo(LocalDate volDate, Long timeSlotId) {
    	TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
    	        .orElseThrow(() -> new IllegalArgumentException("해당 타임슬롯이 존재하지 않습니다."));
    	
        Integer count = volunteerRepository.countByDateAndTimeSlot(volDate, timeSlot);
        
        if (count == null) {
            count = 0;
        }
        
        return VolunteerCountDto.builder()
        		.timeSlotId(timeSlot.getId())
                .label(timeSlot.getLabel())
                .reservedCount(count != null ? count : 0)
                .capacity(timeSlot.getCapacity())  // 예: 봉사 정원
                .build();
    }
    
    // 사용자용 - 봉사 시간대 조회
    @Override
    @Transactional(readOnly = true)
    public List<VolunteerCountDto> getVolunteerTimeSlotsWithCount(LocalDate volDate, Long memberNum) {
        // 봉사 타입 슬롯 전부 가져오기
        List<TimeSlot> timeSlots =
            timeSlotRepository.findByTimeTypeAndEnabledTrueOrderByStartTimeAsc(TimeType.VOL);

        return timeSlots.stream()
                .map(slot -> {
                    Integer reserved = volunteerRepository.countByDateAndTimeSlot(volDate, slot);
                    int reservedCount = reserved != null ? reserved : 0;

                    return VolunteerCountDto.builder()
                            .timeSlotId(slot.getId())
                            .label(slot.getLabel())
                            .reservedCount(reservedCount)
                            .capacity(slot.getCapacity())
                            .build();
                })
                .toList();
    }
}