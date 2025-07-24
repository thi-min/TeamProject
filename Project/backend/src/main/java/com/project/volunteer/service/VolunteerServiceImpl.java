package com.project.volunteer.service;

import com.project.volunteer.dto.VolunteerCountDto;
import com.project.volunteer.dto.VolunteerDetailDto;
import com.project.volunteer.dto.VolunteerRequestDto;
import com.project.volunteer.entity.Volunteer;
import com.project.volunteer.repository.VolunteerRepository;
import com.project.reserve.entity.Reserve;
import com.project.reserve.repository.ReserveRepository;
import com.project.member.entity.MemberEntity;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VolunteerServiceImpl implements VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final ReserveRepository reserveRepository;

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
                .volTime(volunteer.getVolTime())
                .note(reserve.getNote())
                .reserveNumber(reserve.getReserveNumber())
                .build();
    }
    
    //봉사 예약 생성(기본 정보랑 봉사 상세 정보 합친 예약)
    @Override
    public void createVolunteer(Reserve reserve, VolunteerRequestDto volunteerDto) {
        Volunteer volunteer = Volunteer.builder()
                .reserve(reserve)
                .volDate(volunteerDto.getVolDate())
                .volTime(volunteerDto.getVolTime())
                .build();

        volunteerRepository.save(volunteer);
    }
    
    @Override
    @Transactional(readOnly = true)
    public VolunteerCountDto getVolunteerCountInfo(LocalDate volDate, String volTime) {
        Integer count = volunteerRepository.countByDateAndTime(volDate, volTime);
        
        if (count == null) {
            count = 0;
        }
        
        return VolunteerCountDto.builder()
                .volDate(volDate)
                .volTime(volTime)
                .reservedCount(count != null ? count : 0)
                .capacity(10)  // 예: 봉사 정원
                .build();
    }
}