package com.project.land.service;

import com.project.land.dto.LandDetailDto;
import com.project.land.entity.Land;
import com.project.land.repository.LandRepository;
import com.project.reserve.entity.Reserve;
import com.project.reserve.repository.ReserveRepository;
import com.project.member.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LandServiceImpl implements LandService {

    private final LandRepository landRepository;
    private final ReserveRepository reserveRepository;

    @Override
    @Transactional(readOnly = true)
    public LandDetailDto getLandDetailByReserveCode(Long reserveCode) {
        // 1. Land 조회
        Land land = landRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약에 대한 놀이터 정보를 찾을 수 없습니다."));

        // 2. Reserve 조회
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약 정보를 찾을 수 없습니다."));

        // 3. Member 정보
        MemberEntity member = reserve.getMember();

        // 4. 결제 계산
        int basePrice = 2000; // 기준금액 (예: 반려견 1마리 기준)
        int dogCount = land.getAnimalNumber();
        int peopleCount = reserve.getReserveNumber();

        int additionalPrice = (dogCount > 1 ? (dogCount - 1) * 1000 : 0) + peopleCount * 1000;
        int totalPrice = basePrice + additionalPrice;

        // 5. DTO 구성
        return LandDetailDto.builder()
                .memberName(member.getMemberName())
                .contact(member.getMemberPhone())
                .dogType(land.getLandType().name()) // LARGE / SMALL
                .dogCount(dogCount)
                .peopleCount(peopleCount)
                .timeSlot(reserve.getTimeSlot())
                .basePrice(basePrice)
                .additionalPrice(additionalPrice)
                .totalPrice(totalPrice)
                .build();
    }
}