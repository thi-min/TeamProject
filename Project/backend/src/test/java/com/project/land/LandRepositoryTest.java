package com.project.land;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.land.entity.Land;
import com.project.land.entity.LandType;
import com.project.land.repository.LandRepository;
import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberSex;
import com.project.member.entity.MemberState;
import com.project.member.repository.MemberRepository;
import com.project.reserve.entity.Reserve;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.repository.ReserveRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@DisplayName("LandRepository 테스트")
class LandRepositoryTest {

    @Autowired
    private LandRepository landRepository;

    @Autowired
    private ReserveRepository reserveRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("예약코드로 land 조회 - 성공")
    void findByReserveCode_성공() {
        // 1. 회원 저장
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .memberName("김영수")
                .memberId("landtest@test.com")
                .memberPw("1234")
                .memberPhone("01011112222")
                .memberAddress("서울시 관악구")
                .memberBirth(LocalDate.of(2000, 9, 30))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.Man)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build());

        // 2. 예약 저장
        Reserve reserve = reserveRepository.save(Reserve.builder()
                .member(member)
                .applyDate(LocalDateTime.now())
                .reserveType(1)
                .reserveNumber(2)
                .reserveState(ReserveState.ING)
                .note("land 테스트")
                .build());

        // 3. land 저장
        Land land = landRepository.save(Land.builder()
                .reserveCode(reserve.getReserveCode())
                .landDate(LocalDate.of(2025, 8, 15))
                .landTime("09:00 ~ 11:00")
                .landType(LandType.SMALL)
                .animalNumber(1)
                .payNumber(10000)
                .build());

        // 4. 검증
        Optional<Land> found = landRepository.findByReserveCode(reserve.getReserveCode());

        assertThat(found).isPresent();
        assertThat(found.get().getLandDate()).isEqualTo(LocalDate.of(2025, 8, 15));
        assertThat(found.get().getLandTime()).isEqualTo("09:00 ~ 11:00");
        assertThat(found.get().getLandType()).isEqualTo(LandType.SMALL);
        assertThat(found.get().getAnimalNumber()).isEqualTo(1);
    }
}