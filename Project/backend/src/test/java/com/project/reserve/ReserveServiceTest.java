package com.project.reserve;

import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.reserve.dto.FullReserveRequestDto;
import com.project.reserve.dto.ReserveRequestDto;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.service.ReserveService;
import com.project.land.dto.LandRequestDto;
import com.project.land.entity.LandType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ReserveServiceTest {

    @Autowired
    private ReserveService reserveService;

    @Autowired
    private MemberRepository memberRepository;
    
    @Test
    void testCreatePlaygroundReserve() {
        // given: 가상의 회원 생성 및 저장
        MemberEntity member = MemberEntity.builder()
                .memberName("홍길동")
                .memberPhone("010-1234-5678")
                .build();
        memberRepository.save(member);

        // 기본 예약 정보 ReserveRequestDto 생성
        ReserveRequestDto reserveDto = ReserveRequestDto.builder()
                .memberNum(member.getMemberNum())
                .reserveType(1) // 1 = 놀이터 예약
                .reserveNumber(2) //인원수 = 2
                .note("테스트 예약입니다.")
                .build();

        // 놀이터 세부 정보 LandRequestDto 생성
        LandRequestDto landDto = new LandRequestDto();
        landDto.setLandDate(LocalDate.now().plusDays(1)); // 내일 날짜
        landDto.setLandTime("11:00 ~ 13:00");
        landDto.setLandType(LandType.SMALL);
        landDto.setAnimalNumber(2);
        landDto.setPayNumber(10000);

        // FullReserveRequestDto로 통합
        FullReserveRequestDto fullDto = new FullReserveRequestDto();
        fullDto.setReserveDto(reserveDto);
        fullDto.setLandDto(landDto);

        // when: 예약 생성
        Long reserveCode = reserveService.createReserve(fullDto);

        // then: 예약 코드가 잘 생성되었는지 확인
        assertNotNull(reserveCode);
    }
}