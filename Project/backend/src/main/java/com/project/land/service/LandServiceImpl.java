package com.project.land.service;

import com.project.common.dto.TimeSlotDto;
import com.project.common.entity.TimeSlot;
import com.project.common.entity.TimeType;
import com.project.common.repository.TimeSlotRepository;
import com.project.common.util.JasyptUtil;
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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Transactional(readOnly = true) // ✅ 상세조회는 읽기 전용 권장
    public LandDetailDto getLandDetailByReserveCode(Long reserveCode) {
        // 1) 엔티티 조회
    	Land land = landRepository.findById(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("놀이터 예약 정보를 찾을 수 없습니다."));

        Reserve reserve = land.getReserve();
        MemberEntity member = reserve.getMember();

        // 2) 요금 계산 (표시용)
        final int basePrice = 2000;
        int animalNumber   = land.getAnimalNumber();
        int guardianNumber = reserve.getReserveNumber();

        int addDogCnt        = Math.max(animalNumber - 1, 0);
        int additionalDogPrice = addDogCnt * 1000;
        int guardianPrice      = guardianNumber * 1000;
        int additionalPrice    = additionalDogPrice + guardianPrice;
        int totalPrice         = basePrice + additionalPrice;

        // 3) 설명 문자열
        String basePriceDetail = String.format(
                "기본 (%s x 1마리)",
                land.getLandType() == LandType.SMALL ? "소형견" : "대형견"
        );
        String extraPriceDetail = String.format(
                "추가반려견 %d마리 × 1,000원 → %,d원, 보호자 %d명 × 1,000원 → %,d원",
                addDogCnt, additionalDogPrice, guardianNumber, guardianPrice
        );
        
        // 전화번호 복호화
        String decryptedPhone = JasyptUtil.decrypt(member.getMemberPhone());
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        
        // 4) DTO 구성 및 반환
        return LandDetailDto.builder()
        		.reserveCode(reserve.getReserveCode())
                .memberName(member.getMemberName())
                .memberPhone(JasyptUtil.decrypt(member.getMemberPhone()))
                .reserveState(reserve.getReserveState())
                .landDate(land.getLandDate())            
                .applyDate(reserve.getApplyDate())  
                .label(land.getTimeSlot().getLabel())
                .note(reserve.getNote())
                .landType(land.getLandType())
                .animalNumber(land.getAnimalNumber())
                .reserveNumber(reserve.getReserveNumber())
                .basePrice(basePrice)
                .additionalPrice(additionalPrice)
                .totalPrice(totalPrice)
                .basePriceDetail(basePriceDetail)
                .extraPriceDetail(extraPriceDetail)
                .build();
    }
    
    //놀이터 예약 생성(기본 정보랑 놀이터 상세 정보 합친 예약)
    @Override
    @Transactional
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
    // 사용자용 월별 예약마감 확인
    @Override
    public Map<LocalDate, List<LandCountDto>> getLandTimeSlotsByMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        Map<LocalDate, List<LandCountDto>> map = new HashMap<>();

        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate date = ym.atDay(day);

            // 소형견
            List<LandCountDto> smallCounts = landRepository.getLandCountInfo(date, LandType.SMALL);

            // 대형견
            List<LandCountDto> largeCounts = landRepository.getLandCountInfo(date, LandType.LARGE);

            // 합쳐서 날짜별로 저장
            List<LandCountDto> counts = new ArrayList<>();
            counts.addAll(smallCounts);
            counts.addAll(largeCounts);

            map.put(date, counts);
        }

        return map;
    }
    
    // 사용자용 - 놀이터 시간대 조회
    @Override
    @Transactional(readOnly = true)
    public List<LandCountDto> getLandTimeSlotsWithCount(LocalDate landDate, Long memberNum, LandType landType) {
        return landRepository.getLandCountInfo(landDate, landType);
    }
    @Override
    @Transactional(readOnly = true)
    public List<LandCountDto> getLandTimeSlotsWithCount(LocalDate landDate, LandType landType) {
        return landRepository.getLandCountInfo(landDate, landType);
    }
    
}