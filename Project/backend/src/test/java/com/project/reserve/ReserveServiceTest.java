package com.project.reserve;

import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.reserve.dto.FullReserveRequestDto;
import com.project.reserve.dto.ReserveRequestDto;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.service.ReserveService;
import com.project.PetProjectApplication;
import com.project.land.dto.LandRequestDto;
import com.project.land.entity.LandType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.project.PetProjectApplication.class)
@Transactional
public class ReserveServiceTest {

    @Autowired
    private ReserveService reserveService;

    @Autowired
    private MemberRepository memberRepository;
    
    @Test
    void testCreatePlaygroundReserve() {
        try {
            // given: 회원 저장
            MemberEntity member = MemberEntity.builder()
                    .memberName("홍길동")
                    .memberPhone("010-1234-5678")
                    .build();
            memberRepository.save(member);

            // 예약 DTO
            ReserveRequestDto reserveDto = ReserveRequestDto.builder()
                    .memberNum(member.getMemberNum())
                    .reserveType(1)
                    .reserveNumber(2)
                    .note("테스트 예약입니다.")
                    .build();

            LandRequestDto landDto = new LandRequestDto();
            landDto.setLandDate(LocalDate.now().plusDays(1));
            landDto.setLandTime("11:00 ~ 13:00");
            landDto.setLandType(LandType.SMALL);
            landDto.setAnimalNumber(2);
            landDto.setPayNumber(10000);

            FullReserveRequestDto fullDto = new FullReserveRequestDto();
            fullDto.setReserveDto(reserveDto);
            fullDto.setLandDto(landDto);

            // when
            Long reserveCode = reserveService.createReserve(fullDto);
            assertNotNull(reserveCode);
        } catch (Exception e) {
            e.printStackTrace(); // 콘솔에 에러 출력
            fail("테스트 중 예외 발생: " + e.getMessage());
        }
    }
}