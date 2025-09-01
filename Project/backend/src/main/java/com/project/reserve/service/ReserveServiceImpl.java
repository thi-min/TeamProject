package com.project.reserve.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.common.entity.TimeSlot;
import com.project.common.entity.TimeType;
import com.project.common.repository.TimeSlotRepository;
import com.project.common.util.JasyptUtil;
import com.project.land.dto.LandDetailDto;
import com.project.land.dto.LandRequestDto;
import com.project.land.entity.Land;
import com.project.land.entity.LandType;
import com.project.land.repository.LandRepository;
import com.project.land.service.LandService;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.reserve.dto.AdminReservationListDto;
import com.project.reserve.dto.AdminReservationSearchDto;
import com.project.reserve.dto.FullReserveRequestDto;
import com.project.reserve.dto.ReserveCompleteResponseDto;
import com.project.reserve.dto.ReserveResponseDto;
import com.project.reserve.entity.Reserve;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.exception.DuplicateReservationException;
import com.project.reserve.repository.ReserveRepository;
import com.project.volunteer.dto.VolunteerDetailDto;
import com.project.volunteer.dto.VolunteerRequestDto;
import com.project.volunteer.entity.Volunteer;
import com.project.volunteer.repository.VolunteerRepository;
import com.project.volunteer.service.VolunteerService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ReserveServiceImpl implements ReserveService {

    private final ReserveRepository reserveRepository;
    private final MemberRepository memberRepository;
    private final LandService landService; 
    private final VolunteerService volunteerService;
    private final TimeSlotRepository timeSlotRepository;
   
    private final LandRepository landRepository;
    private final VolunteerRepository volunteerRepository;


    
    
    // 예약생성 (사용자가 예약요청하면 예약상태 기본값으로 설정, DB에 저장)
    @Override
    @Transactional
    public ReserveCompleteResponseDto createReserve(FullReserveRequestDto fullRequestDto) {

        if (fullRequestDto == null || fullRequestDto.getReserveDto() == null) {
            throw new IllegalArgumentException("예약 정보가 잘못되었습니다.");
        }

        Long memberNum = fullRequestDto.getReserveDto().getMemberNum();
        MemberEntity member = memberRepository.findById(memberNum)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        int reserveType = fullRequestDto.getReserveDto().getReserveType();

        // 보호자 수 유효성 검사
        int reserveNumber = fullRequestDto.getReserveDto().getReserveNumber();
        if (reserveNumber <= 0) {
            throw new IllegalArgumentException("보호자 수는 최소 1명 이상이어야 합니다.");
        }

        String message;
        TimeSlot timeSlot;

        // 중복 예약 검사 + 시간대 유효성 검사
        if (reserveType == 1) { // LAND
            LandRequestDto landDto = fullRequestDto.getLandDto();
            if (landDto == null) {
                throw new IllegalArgumentException("놀이터 예약 세부 정보가 누락되었습니다.");
            }

            Long timeSlotId = landDto.getTimeSlotId();

            // ✅ 시간대 유효성 검사
            boolean validSlot = timeSlotRepository.existsByIdAndTimeTypeAndEnabled(
                    timeSlotId,
                    TimeType.LAND,
                    true
            );
            if (!validSlot) {
                throw new IllegalArgumentException("선택한 시간대는 놀이터 예약에 유효하지 않습니다.");
            }

            // ✅ 중복 예약 검사
            boolean exists = reserveRepository
            	    .existsByMember_MemberNumAndLandDetail_TimeSlot_IdAndLandDetail_LandDateAndReserveStateIn(
            	        memberNum,
            	        timeSlotId,
            	        landDto.getLandDate(),
            	        List.of(ReserveState.ING, ReserveState.DONE)
            	    );
            if (exists) {
                throw new DuplicateReservationException("이미 해당 시간에 놀이터 예약이 존재합니다.");
            }

            timeSlot = timeSlotRepository.findById(timeSlotId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 시간대 정보를 찾을 수 없습니다."));

        } else if (reserveType == 2) { // VOLUNTEER
            VolunteerRequestDto volunteerDto = fullRequestDto.getVolunteerDto();
            if (volunteerDto == null) {
                throw new IllegalArgumentException("봉사 예약 세부 정보가 누락되었습니다.");
            }

            Long timeSlotId = volunteerDto.getTimeSlotId();

            // ✅ 시간대 유효성 검사
            boolean validSlot = timeSlotRepository.existsByIdAndTimeTypeAndEnabled(
                    timeSlotId,
                    TimeType.VOL,
                    true
            );
            if (!validSlot) {
                throw new IllegalArgumentException("선택한 시간대는 봉사 예약에 유효하지 않습니다.");
            }

            // ✅ 중복 예약 검사
            boolean exists = reserveRepository
            	    .existsByMember_MemberNumAndVolunteerDetail_TimeSlot_IdAndVolunteerDetail_VolDateAndReserveStateIn(
            	        memberNum,
            	        timeSlotId,
            	        volunteerDto.getVolDate(),
            	        List.of(ReserveState.ING, ReserveState.DONE)
            	    );
            if (exists) {
                throw new DuplicateReservationException("이미 해당 시간에 봉사 예약이 존재합니다.");
            }

            timeSlot = timeSlotRepository.findById(timeSlotId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 시간대 정보를 찾을 수 없습니다."));
        } else {
            throw new IllegalArgumentException("예약 유형이 유효하지 않습니다.");
        }

        // ✅ 예약 저장
        Reserve reserve = fullRequestDto.getReserveDto().toEntity(member);
        Reserve saved = reserveRepository.save(reserve);

        // ✅ 세부 정보 저장
        if (reserveType == 1) {
            landService.createLand(saved, fullRequestDto.getLandDto(), timeSlot);
            message = "놀이터 예약이 완료되었습니다.";
        } else {
            volunteerService.createVolunteer(saved, fullRequestDto.getVolunteerDto(), timeSlot);
            message = "봉사활동 신청이 완료되었습니다.";
        }

        return ReserveCompleteResponseDto.builder()
                .reserveCode(saved.getReserveCode())
                .message(message)
                .build();
    }
    
    //특정회원(membernum)이 신청한 예약 목록 조회
    //마이페이지에 사용
    @Override
    @Transactional(readOnly = true)
    public List<ReserveResponseDto> getReservesByMember(Long memberNum) {
    	//존재하지않는 회원인 경우 예외 처리
    	if (!memberRepository.existsById(memberNum)) {
            throw new IllegalArgumentException("존재하지 않는 회원 번호입니다.");
        }
        return reserveRepository.findByMember_MemberNum(memberNum).stream()
                .map(ReserveResponseDto::from)
                .collect(Collectors.toList());
    }
    
    //관리자 전체 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<AdminReservationListDto> getAllReservationsForAdmin() {
        List<Reserve> reserveList = reserveRepository.findAllWithDetails(); // 엔티티만 가져옴
        return reserveList.stream()
                .map(AdminReservationListDto::from) // DTO로 가공
                .collect(Collectors.toList());
    }
    
    //관리자 놀이터 예약목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<AdminReservationListDto> getLandReservationsForAdmin() {
        return reserveRepository.findLandReservationsForAdmin().stream()
                .map(AdminReservationListDto::from)
                .collect(Collectors.toList());
    }
    
    //관리자 봉사 예약목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<AdminReservationListDto> getVolunteerReservationsForAdmin() {
        return reserveRepository.findVolunteerReservationsForAdmin().stream()
                .map(AdminReservationListDto::from)
                .collect(Collectors.toList());
    }
    
    //사용자 놀이터예약 상세페이지
    @Override
    @Transactional(readOnly = true)
    public LandDetailDto getMemberLandReserveDetail(Long reserveCode, Long memberNum) {
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        if (!reserve.getMember().getMemberNum().equals(memberNum)) {
            throw new SecurityException("본인의 예약만 조회할 수 있습니다.");
        }
        
        Land land = landRepository.findById(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("놀이터 예약 정보를 찾을 수 없습니다."));
        
        return landService.getLandDetailByReserveCode(reserveCode);
    }
    // 사용자 - 봉사예약 상세페이지
    @Override
    @Transactional(readOnly = true)
    public VolunteerDetailDto getMemberVolunteerReserveDetail(Long reserveCode, Long memberNum) {
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        if (!reserve.getMember().getMemberNum().equals(memberNum)) {
            throw new SecurityException("본인의 예약만 조회할 수 있습니다.");
        }

        return volunteerService.getVolunteerDetailByReserveCode(reserveCode);
    }
    
    //관리자용 놀이터 예약 상세보기
    @Override
    @Transactional(readOnly = true)
    public LandDetailDto getAdminLandReserveDetail(Long reserveCode) {
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));
        
        MemberEntity member = reserve.getMember();
        Land land = reserve.getLandDetail();
        
        // 요금 계산 (표시용)
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
        
        return LandDetailDto.builder()
        	    .reserveCode(reserve.getReserveCode())
        	    .memberName(member.getMemberName())
        	    .memberPhone(decryptedPhone) 
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
        	    .basePriceDetail(basePriceDetail)
                .extraPriceDetail(extraPriceDetail)
        	    .build();

    }
    
    //관리자용 봉사 예약 상세보기
    @Override
    @Transactional(readOnly = true)
    public VolunteerDetailDto getAdminVolunteerReserveDetail(Long reserveCode) {
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        MemberEntity member = reserve.getMember();
        Volunteer volunteer = reserve.getVolunteerDetail();
        
        // 전화번호 복호화
        String decryptedPhone = JasyptUtil.decrypt(member.getMemberPhone());
        
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
    //사용자가 자신의 예약을 취소할때 사용
    @Override
    @Transactional
    public void memberCancelReserve(Long reserveCode, Long memberNum) {
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));
        if (!reserve.getMember().getMemberNum().equals(memberNum)) {
            throw new SecurityException("본인의 예약만 취소할 수 있습니다.");
        }
        
        //이미 취소된 상태일 경우 예외처리
        if (reserve.getReserveState() == ReserveState.CANCEL) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }
        reserve.setReserveState(ReserveState.CANCEL);
    }
    
    // 관리자가 검색할때 검색필터링 (기간, 예약코드, 회원명, 예약상태)
    //놀이터
    @Override
    @Transactional(readOnly = true)
    public List<AdminReservationListDto> searchLandReservationsForAdmin(AdminReservationSearchDto dto) {
        return reserveRepository.searchLandReservations(
                dto.getReserveCode(),
                dto.getMemberName(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getReserveState()
        ).stream()
         .map(AdminReservationListDto::from)
         .collect(Collectors.toList());
    }
    
    //봉사
    @Override
    @Transactional(readOnly = true)
    public List<AdminReservationListDto> searchVolunteerReservationsForAdmin(AdminReservationSearchDto dto) {
        return reserveRepository.searchVolunteerReservations(
                dto.getReserveCode(),
                dto.getMemberName(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getReserveState()
        ).stream()
         .map(AdminReservationListDto::from)
         .collect(Collectors.toList());
    }
    
    //관리자가 특정 예약의 상태를 변경
    @Override
    @Transactional
    public void updateReserveStateByAdmin(Long reserveCode, ReserveState newState) {
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));
        // 현재 상태
        ReserveState cur = reserve.getReserveState();

        // 취소건을 진행/완료로 변경 시도 → 예외
        if (cur == ReserveState.CANCEL && (newState == ReserveState.ING || newState == ReserveState.DONE)) {
            throw new IllegalArgumentException("취소된 예약은 진행/완료로 변경할 수 없습니다.");
        }

        reserve.setReserveState(newState);
        reserve.setUpdateTime(LocalDateTime.now());
    }

    //마이페이지에서 예약유형 별 탭 기능
    @Override
    @Transactional(readOnly = true)
    public List<ReserveResponseDto> getReservesByMemberAndType(Long memberNum, int type) {
    	//존재하지 않는 회원인 경우 예외처리
    	if (!memberRepository.existsById(memberNum)) {
            throw new IllegalArgumentException("존재하지 않는 회원 번호입니다.");
        }
        return reserveRepository.findByMember_MemberNumAndReserveType(memberNum, type).stream()
                .map(ReserveResponseDto::from)
                .collect(Collectors.toList());
    }
    // 놀이터 예약 중복검사(formpage->confirmpage넘어갈때)
    @Override
    public boolean existsLandDuplicate(Long memberNum, LocalDate date, Long timeSlotId) {
        return reserveRepository.existsByMember_MemberNumAndLandDetail_TimeSlot_IdAndLandDetail_LandDateAndReserveStateIn(
            memberNum, timeSlotId, date, List.of(ReserveState.ING, ReserveState.DONE)
        );
    }

    // 봉사 예약 중복검사(formpage->confirmpage넘어갈때)
    @Override
    public boolean existsVolunteerDuplicate(Long memberNum, LocalDate date, Long timeSlotId) {
        return reserveRepository.existsByMember_MemberNumAndVolunteerDetail_TimeSlot_IdAndVolunteerDetail_VolDateAndReserveStateIn(
            memberNum, timeSlotId, date, List.of(ReserveState.ING, ReserveState.DONE)
        );
    }
  
}