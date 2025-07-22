package com.project.land.service;

import com.project.land.dto.LandDetailDto;
import com.project.land.dto.LandRequestDto;
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
        Reserve reserve = land.getReserve();

        // 3. Member 정보
        MemberEntity member = reserve.getMember();

        // 4. 결제 계산
        int basePrice = 2000; // 기준금액 (예: 반려견 1마리 기준)
        int AnimalNumber = land.getAnimalNumber();
        int ReserveNumber = reserve.getReserveNumber();
        
        // 반려견 수가 2마리 이상이라면 추가 반려견 수 만큼 1000원씩 요금 부가 + 보호자 수 만큼 인당 1000원 부가
        int additionalPrice = (AnimalNumber > 1 ? (AnimalNumber - 1) * 1000 : 0) + ReserveNumber * 1000;
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
    
    //놀이터 예약 생성(기본 정보랑 놀이터 상세 정보 합친 예약)
    @Override
    public void createLand(Reserve reserve, LandRequestDto landDto) {
        Land land = Land.builder()
                .reserve(reserve)
                .landDate(landDto.getLandDate())
                .landTime(landDto.getLandTime())
                .landType(landDto.getLandType())
                .animalNumber(landDto.getAnimalNumber())
                .payNumber(landDto.getPayNumber())
                .build();

        landRepository.save(land);
    }
}