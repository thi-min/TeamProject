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
        		.reserveCode(reserve.getReserveCode())
                .memberName(member.getMemberName())
                .phone(member.getMemberPhone())
                .reserveState(reserve.getReserveState())
                .landDate(land.getLandDate())
                .landTime(land.getLandTime())
                .applyDate(reserve.getApplyDate())
                .note(reserve.getNote())
                .landType(land.getLandType())
                .animalNumber(land.getAnimalNumber())
                .reserveNumber(reserve.getReserveNumber())
                .basePrice(2000)
                .additionalPrice(1000 * (reserve.getReserveNumber() - 1))
                .totalPrice(2000 + 1000 * (reserve.getReserveNumber() - 1))
                .basePriceDetail("중, 소형견 x " + land.getAnimalNumber() + "마리")
                .extraPriceDetail(" 추가 인원 x" + reserve.getReserveNumber() + "명")
                .build();
    }
}