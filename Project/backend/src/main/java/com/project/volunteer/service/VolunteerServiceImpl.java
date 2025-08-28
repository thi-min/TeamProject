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
import com.project.common.util.JasyptUtil;

import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VolunteerServiceImpl implements VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final ReserveRepository reserveRepository;
    private final TimeSlotRepository timeSlotRepository;
    
    //봉사 상세보기 화면
    @Override
    @Transactional(readOnly = true)
    public VolunteerDetailDto getVolunteerDetailByReserveCode(Long reserveCode) {
        // 1. Volunteer 조회
        Volunteer volunteer = volunteerRepository.findById(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("봉사 예약 정보를 찾을 수 없습니다."));

        // 2. Reserve 조회
        Reserve reserve = volunteer.getReserve();

        // 3. 회원 정보 조회
        MemberEntity member = reserve.getMember();
        
        // 전화번호 복호화
        String decryptedPhone = JasyptUtil.decrypt(member.getMemberPhone());
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        
        // 4. DTO 생성 및 반환
        return VolunteerDetailDto.builder()
                .reserveCode(reserve.getReserveCode())
                .memberName(member.getMemberName())
                .memberPhone(decryptedPhone)
                .memberBirth(member.getMemberBirth())
                .reserveState(reserve.getReserveState())
                .volDate(volunteer.getVolDate())
                .applyDate(reserve.getApplyDate())  
                .label(volunteer.getTimeSlot().getLabel())
                .note(reserve.getNote())
                .reserveNumber(reserve.getReserveNumber())
                .build();
    }
    
    //봉사 예약 생성(기본 정보랑 봉사 상세 정보 합친 예약)
    @Override
    @Transactional
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
    
    //사용자 월별 예약마감 확인
    public Map<LocalDate, List<VolunteerCountDto>> getVolunteerTimeSlotsByMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        Map<LocalDate, List<VolunteerCountDto>> map = new HashMap<>();

        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate date = ym.atDay(day);
            // 주말만 조회해도 됨 (프론트 규칙 반영하려면)
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                List<VolunteerCountDto> counts = getVolunteerTimeSlotsWithCount(date);
                map.put(date, counts);
            }
        }
        return map;
    }
    
    // 사용자용 - 봉사 시간대 조회
    @Override
    @Transactional(readOnly = true)
    public List<VolunteerCountDto> getVolunteerTimeSlotsWithCount(LocalDate volDate, Long memberNum) {
        return volunteerRepository.getVolunteerCountInfo(volDate);
    }
    @Override
    @Transactional(readOnly = true)
    public List<VolunteerCountDto> getVolunteerTimeSlotsWithCount(LocalDate volDate) {
        return volunteerRepository.getVolunteerCountInfo(volDate);
    }
}