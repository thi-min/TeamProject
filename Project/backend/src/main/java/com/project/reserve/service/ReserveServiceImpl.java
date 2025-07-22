package com.project.reserve.service;

import com.project.reserve.dto.AdminReservationListDto;
import com.project.reserve.dto.AdminReservationSearchDto;
import com.project.reserve.dto.ReserveRequestDto;
import com.project.reserve.dto.ReserveResponseDto;
import com.project.reserve.entity.Reserve;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.repository.ReserveRepository;
import com.project.volunteer.dto.VolunteerDetailDto;
import com.project.volunteer.entity.Volunteer;
import com.project.member.repository.MemberRepository;
import com.project.land.dto.LandDetailDto;
import com.project.land.entity.Land;
import com.project.land.service.LandService;
import com.project.member.entity.MemberEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReserveServiceImpl implements ReserveService {

    private final ReserveRepository reserveRepository;
    private final MemberRepository memberRepository;
    private final LandService landService; 

    // 사용자가 예약요청하면 예약상태 기본값으로 설정, DB에 저장
    @Override
    @Transactional
    public Long createReserve(ReserveRequestDto requestDto) {
        MemberEntity member = memberRepository.findById(requestDto.getMemberNum())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        Reserve reserve = requestDto.toEntity(member);
        Reserve saved = reserveRepository.save(reserve);

        return saved.getReserveCode();
    }
    
    //특정회원(membernum)이 신청한 예약 목록 조회
    //마이페이지에 사용
    @Override
    @Transactional(readOnly = true)
    public List<ReserveResponseDto> getReservesByMember(Long memberNum) {
        return reserveRepository.findByMember_MemberNum(memberNum).stream()
                .map(ReserveResponseDto::from)
                .collect(Collectors.toList());
    }
    
    //관리자 전체 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<AdminReservationListDto> getAllReservationsForAdmin() {
        return reserveRepository.findAllReservationsForAdmin(); // @Query 기반
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

        MemberEntity member = reserve.getMember();
        Volunteer volunteer = reserve.getVolunteerDetail();

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
    
    //관리자용 예약 상세보기
    @Override
    @Transactional(readOnly = true)
    public LandDetailDto getAdminLandReserveDetail(Long reserveCode) {
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        MemberEntity member = reserve.getMember();
        Land land = reserve.getLandDetail();

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
                .basePrice(2000)			//기본가격
                .additionalPrice(1000 * (reserve.getReserveNumber() - 1))	//추가요금
                .totalPrice(2000 + 1000 * (reserve.getReserveNumber() - 1)) 	//총 결제금액
                .basePriceDetail("중, 소형견 x " + land.getAnimalNumber() + "마리")
                .extraPriceDetail(" 추가 인원 x" + reserve.getReserveNumber() + "명")
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
    //사용자가 자신의 예약을 취소할때 사용
    @Override
    @Transactional
    public void memberCancelReserve(Long reserveCode, Long memberNum) {
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));
        if (!reserve.getMember().getMemberNum().equals(memberNum)) {
            throw new SecurityException("본인의 예약만 취소할 수 있습니다.");
        }
        reserve.setReserveState(ReserveState.CANCEL);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AdminReservationListDto> searchReservationsForAdmin(AdminReservationSearchDto searchDto) {
        return reserveRepository.searchBar(
                searchDto.getReserveCode(),
                searchDto.getMemberName(),
                searchDto.getStartDate(),
                searchDto.getEndDate(),
                searchDto.getReserveState()
        ).stream()
         .map(AdminReservationListDto::from)  // 필요시 from() 메서드 사용
         .collect(Collectors.toList());
    }
    
    //관리자가 특정 예약의 상태를 직접 변경
    @Override
    @Transactional
    public void updateReserveStateByAdmin(Long reserveCode, ReserveState newState) {
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));
        reserve.setReserveState(newState);
    }
    
   
    //예약 유형에 따라 조회 (사용자)
    @Override
    @Transactional(readOnly = true)
    public List<ReserveResponseDto> getReservesByType(int type) {
        return reserveRepository.findByReserveType(type).stream()
                .map(ReserveResponseDto::from)
                .collect(Collectors.toList());
    }
}