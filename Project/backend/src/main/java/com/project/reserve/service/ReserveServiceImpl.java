package com.project.reserve.service;

import com.project.reserve.dto.ReserveRequestDto;
import com.project.reserve.dto.ReserveResponseDto;
import com.project.reserve.entity.Reserve;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.repository.ReserveRepository;
//import com.project.member.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReserveServiceImpl implements ReserveService {

    private final ReserveRepository reserveRepository;
//    private final MemberRepository memberRepository;

      // 사용자가 예약요청하면 예약상태 기본값으로 설정, 저장
//    @Override
//    @Transactional
//    public Long createReserve(ReserveRequestDto requestDto) {
//        Member member = memberRepository.findById(requestDto.getMemberNum())
//                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
//
//        Reserve reserve = requestDto.toEntity(member);
//        Reserve saved = reserveRepository.save(reserve);
//
//        return saved.getReserveCode();
//    }
    //
    @Override
    @Transactional(readOnly = true)
    public ReserveResponseDto getReserveByCode(Long reserveCode) {
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));
        return ReserveResponseDto.from(reserve);
    }
    //특정회원의 모든 예약 정보 가져오기
    @Override
    @Transactional(readOnly = true)
    public List<ReserveResponseDto> getReservesByMember(Long memberNum) {
        List<Reserve> reserves = reserveRepository.findByMember_MemberNum(memberNum); //실제 DB에서 회원 목록 조회
        return reserves.stream()
                .map(ReserveResponseDto::from) //ReserveResponseDto from(Reserve reserve)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelReserve(Long reserveCode) {
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        reserve.setReserveState(ReserveState.REJ); // 취소 = 거절 처리
    }

    @Override
    @Transactional
    public void updateReserveState(Long reserveCode, ReserveState newState) {
        Reserve reserve = reserveRepository.findByReserveCode(reserveCode)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        reserve.setReserveState(newState);
    }
}