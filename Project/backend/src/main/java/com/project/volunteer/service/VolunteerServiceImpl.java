package com.project.volunteer.service;

import com.project.volunteer.dto.VolunteerDetailDto;
import com.project.volunteer.entity.Volunteer;
import com.project.volunteer.repository.VolunteerRepository;
import com.project.reserve.entity.Reserve;
import com.project.reserve.repository.ReserveRepository;
import com.project.member.entity.MemberEntity;

import lombok.RequiredArgsConstructor;
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
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약 정보를 찾을 수 없습니다."));

        // 3. 회원 정보 조회
        MemberEntity member = reserve.getMember();

        // 4. DTO 생성 및 반환
        return VolunteerDetailDto.builder()
                .memberName(member.getMemberName())
                .contact(member.getMemberPhone())
                .reserveDate(reserve.getReserveDate())
                .timeSlot(reserve.getTimeSlot())
                .schedule(volunteer.getSchedule())
                .build();
    }
}