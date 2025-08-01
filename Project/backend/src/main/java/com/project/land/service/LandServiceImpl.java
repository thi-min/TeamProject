package com.project.land.service;

import com.project.common.dto.TimeSlotDto;
import com.project.common.entity.TimeSlot;
import com.project.common.repository.TimeSlotRepository;
import com.project.land.dto.LandCountDto;
import com.project.land.dto.LandDetailDto;
import com.project.land.dto.LandRequestDto;
import com.project.land.entity.Land;
import com.project.land.entity.LandType;
import com.project.land.repository.LandRepository;
import com.project.reserve.entity.Reserve;
import com.project.reserve.repository.ReserveRepository;
import com.project.member.entity.MemberEntity;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LandServiceImpl implements LandService {

    private final LandRepository landRepository;
    private final ReserveRepository reserveRepository;
    private final TimeSlotRepository timeSlotRepository;

    
    // 놀이터 예약 상세보기 화면
    @Override
    @Transactional(readOnly = false)
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
        int animalNumber = land.getAnimalNumber();
        int reserveNumber = reserve.getReserveNumber();
        
        // 반려견 수가 2마리 이상이라면 추가 반려견 수 만큼 1000원씩 요금 부가 + 보호자 수 만큼 인당 1000원 부가
        int additionalPrice = (animalNumber > 1 ? (animalNumber - 1) * 1000 : 0) + reserveNumber * 1000;
        int totalPrice = basePrice + additionalPrice;
        
        land.setPayNumber(totalPrice);	//totalprice를 paynumber로
        landRepository.save(land);
        // 5. DTO 구성
        return LandDetailDto.builder()
        		.reserveCode(reserve.getReserveCode())
                .memberName(member.getMemberName())
                .phone(member.getMemberPhone())
                .reserveState(reserve.getReserveState())
                .landDate(land.getLandDate())
                .label(land.getTimeSlot().getLabel())
                .applyDate(reserve.getApplyDate())
                .note(reserve.getNote())
                .landType(land.getLandType())
                .animalNumber(land.getAnimalNumber())
                .reserveNumber(reserve.getReserveNumber())
                .basePrice(basePrice)
                .additionalPrice(additionalPrice)
                .totalPrice(totalPrice)
                .basePriceDetail("반려견 x " + land.getAnimalNumber() + "마리")
                .extraPriceDetail(" 추가 인원 x" + reserve.getReserveNumber() + "명")
                .build();
    }
    
    //놀이터 예약 생성(기본 정보랑 놀이터 상세 정보 합친 예약)
    @Override
    public void createLand(Reserve reserve, LandRequestDto landDto, TimeSlot timeSlot) {
        Land land = Land.builder()
                .reserve(reserve)
                .landDate(landDto.getLandDate())
                .timeSlot(timeSlot)
                .landType(landDto.getLandType())
                .animalNumber(landDto.getAnimalNumber())
                .payNumber(landDto.getPayNumber())
                .build();
        
        reserve.setLandDetail(land); //reserve -> land
        
        landRepository.save(land);	//land->reserve 
    }
    
    // 예약 마리수 체크
    @Override
    @Transactional(readOnly = true)
    public LandCountDto getLandCountInfo(LocalDate landDate, Long timeSlotId, LandType landType) {
    	TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
    	        .orElseThrow(() -> new IllegalArgumentException("해당 타임슬롯이 존재하지 않습니다."));
    	
        Integer count = landRepository.countByDateAndTimeAndType(landDate, timeSlot, landType);

        if (count == null) {
            count = 0;
        }

        int capacity = (landType == LandType.SMALL) ? 15 : 10; // 유형별 정원 설정

        return LandCountDto.builder()
                .landDate(landDate)
                .label(timeSlot.getLabel()) // DTO에서 label로 수정한 경우
                .landType(landType)
                .reservedCount(count)
                .capacity(capacity)
                .build();
    }
    
    
}