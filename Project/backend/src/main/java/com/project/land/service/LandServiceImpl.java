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
        int basePrice = 2000;
        int animalNumber = landDto.getAnimalNumber();
        int reserveNumber = reserve.getReserveNumber();

        int additionalPrice = (animalNumber > 1 ? (animalNumber - 1) * 1000 : 0) + reserveNumber * 1000;
        int totalPrice = basePrice + additionalPrice;

        Land land = Land.builder()
                .reserve(reserve)
                .landDate(landDto.getLandDate())
                .timeSlot(timeSlot)
                .landType(landDto.getLandType())
                .animalNumber(animalNumber)
                .payNumber(totalPrice) // ✅ 서버에서 계산된 금액
                .build();

        reserve.setLandDetail(land);
        landRepository.save(land);
    }
    
    // 관리자용 - 단일 시간대에 대해 정원 및 현재 예약 수 조회, 시간대별 예약현황 조회
    @Override
    public LandCountDto getLandCountForSlot(LocalDate landDate, Long timeSlotId, LandType landType) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new IllegalArgumentException("해당 시간대가 존재하지 않습니다."));

        Integer reserved = landRepository.countByDateAndTimeAndType(landDate, timeSlot, landType);
        int reservedCount = reserved != null ? reserved : 0;

        return LandCountDto.builder()
                .timeSlotId(timeSlot.getId())
                .label(timeSlot.getLabel())
                .landType(landType)
                .reservedCount(reservedCount)
                .capacity(timeSlot.getCapacity())
                .build();
    }
    
    // 사용자용 - 프론트에서 시간대 선택 ui 구성때 사용
    @Override
    public List<LandCountDto> getLandTimeSlotsWithCount(LocalDate landDate, Long memberNum, LandType landType) {
        List<TimeSlot> timeSlots = timeSlotRepository.findAll(); // 필터링 필요시 LAND 전용만

        return timeSlots.stream()
                .map(timeSlot -> {
                    Integer reserved = landRepository.countByDateAndTimeAndType(landDate, timeSlot, landType);
                    int reservedCount = reserved != null ? reserved : 0;

                    return LandCountDto.builder()
                            .timeSlotId(timeSlot.getId())
                            .label(timeSlot.getLabel())
                            .landType(landType)
                            .reservedCount(reservedCount)
                            .capacity(timeSlot.getCapacity())
                            .build();
                })
                .collect(Collectors.toList());
    }
    
}