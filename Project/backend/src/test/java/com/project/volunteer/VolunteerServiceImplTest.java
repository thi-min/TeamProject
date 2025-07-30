package com.project.volunteer;

import com.project.member.entity.*;
import com.project.member.repository.MemberRepository;
import com.project.reserve.entity.Reserve;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.repository.ReserveRepository;
import com.project.volunteer.dto.VolunteerCountDto;
import com.project.volunteer.entity.Volunteer;
import com.project.volunteer.repository.VolunteerRepository;
import com.project.volunteer.service.VolunteerService;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
@DisplayName("VolunteerServiceImpl 테스트")
public class VolunteerServiceImplTest {
	
	@Autowired
    private VolunteerService volunteerService;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private ReserveRepository reserveRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("봉사 인원수 조회 기능 테스트 - 성공")
    void getVolunteerCountInfo_성공() {
        // given - 회원 정보 저장
        MemberEntity member1 = memberRepository.save(MemberEntity.builder()
                .memberName("김민준")
                .memberId("mjkim@test.com")
                .memberPw("pw1234")
                .memberPhone("01012345678")
                .memberAddress("서울시 강남구")
                .memberBirth(LocalDate.of(1995, 1, 1))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.MAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build());

        MemberEntity member2 = memberRepository.save(MemberEntity.builder()
                .memberName("이지은")
                .memberId("jieun@test.com")
                .memberPw("pw5678")
                .memberPhone("01098765432")
                .memberAddress("서울시 마포구")
                .memberBirth(LocalDate.of(1998, 5, 10))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.WOMAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build());

        // given - 기준 날짜 및 시간
        LocalDate targetDate = LocalDate.of(2025, 9, 15);
        String targetTime = "13:00 ~ 15:00";

        // 봉사 예약 1
        Reserve reserve1 = reserveRepository.save(Reserve.builder()
                .member(member1)
                .applyDate(LocalDateTime.now())
                .reserveType(2)
                .reserveNumber(2)
                .reserveState(ReserveState.ING)
                .note("봉사예약 1")
                .build());

        volunteerRepository.save(Volunteer.builder()
                .volDate(targetDate)
                .volTime(targetTime)
                .reserve(reserve1)
                .build());

        // 봉사 예약 2
        Reserve reserve2 = reserveRepository.save(Reserve.builder()
                .member(member2)
                .applyDate(LocalDateTime.now())
                .reserveType(2)
                .reserveNumber(3)
                .reserveState(ReserveState.ING)
                .note("봉사예약 2")
                .build());

        volunteerRepository.save(Volunteer.builder()
                .volDate(targetDate)
                .volTime(targetTime)
                .reserve(reserve2)
                .build());

        // when - 서비스 호출
        VolunteerCountDto result = volunteerService.getVolunteerCountInfo(targetDate, targetTime);

        // then - 검증
        assertThat(result).isNotNull();
        assertThat(result.getVolDate()).isEqualTo(targetDate);
        assertThat(result.getVolTime()).isEqualTo(targetTime);
        assertThat(result.getReservedCount()).isEqualTo(5); // 2 + 3
        assertThat(result.getCapacity()).isEqualTo(10);
    }
}


