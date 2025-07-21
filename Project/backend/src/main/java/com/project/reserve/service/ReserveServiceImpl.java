package com.project.reserve.service;

import com.project.reserve.dto.AdminReservationListDto;
import com.project.reserve.dto.ReserveDetailDto;
import com.project.reserve.dto.ReserveRequestDto;
import com.project.reserve.dto.ReserveResponseDto;
import com.project.reserve.entity.Reserve;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.repository.ReserveRepository;
import com.project.member.repository.MemberRepository;
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
                .map(reserve -> {
                    ReserveResponseDto dto = ReserveResponseDto.from(reserve);
                    dto.setProgramName(convertReserveType(reserve.getReserveType())); 
                    return dto;
                }).collect(Collectors.toList());
    }
    
    //관리자 전체 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<AdminReservationListDto> getAllReservationsForAdmin() {
        return reserveRepository.findAllReservationsForAdmin(); // @Query 기반
    }
    
    //사용자용 예약 상세보기
    @Override
    @Transactional(readOnly = true)
    public ReserveDetailDto getMemberReserveByCode(Long reserveCode, Long memberNum) {
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        if (!reserve.getMember().getMemberNum().equals(memberNum)) {
            throw new SecurityException("본인의 예약만 조회할 수 있습니다.");
        }

        ReserveResponseDto dto = ReserveResponseDto.from(reserve);
        dto.setProgramName(convertReserveType(reserve.getReserveType()));
        return dto;
    }
    
    //관리자용 예약 상세보기
    @Override
    @Transactional(readOnly = true)
    public ReserveDetailDto getAdminReserveByCode(Long reserveCode) {
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        return ReserveDetailDto.builder()
                .reserveCode(reserve.getReserveCode())
                .memberName(reserve.getMember().getMemberName())
                .phone(reserve.getMember().getMemberPhone())
                .programName(convertReserveType(reserve.getReserveType()))
                .dogCount(reserve.getReserveNumber())
                .peopleCount(reserve.getPeopleCount())
                .applyDate(reserve.getApplyDate())
                .reserveDate(reserve.getReserveDate())
                .reserveTime(reserve.getTimeSlot())
                .note(reserve.getNote())
                .basePrice(2000)
                .extraPrice(1000 * (reserve.getPeopleCount() - 1))
                .totalPrice(2000 + 1000 * (reserve.getPeopleCount() - 1))
                .basePriceDetail("중, 소형견 x " + reserve.getReserveNumber() + "마리")
                .extraPriceDetail("인원추가 x " + (reserve.getPeopleCount() - 1) + "명")
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
    
    //관리자가 특정 예약의 상태를 직접 변경
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
                .map(reserve -> {
                    ReserveResponseDto dto = ReserveResponseDto.from(reserve);
                    dto.setProgramName(convertReserveType(reserve.getReserveType())); 
                    return dto;
                }).collect(Collectors.toList());
    }

    
    private String convertReserveType(int typeCode) {
        return switch (typeCode) {
            case 1 -> "놀이터 예약";
            case 2 -> "봉사활동";
            default -> "알 수 없는 예약 유형";
        };
    }
}