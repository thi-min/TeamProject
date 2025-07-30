package com.project.volunteer;

import com.project.member.entity.*;
import com.project.member.repository.MemberRepository;
import com.project.reserve.entity.Reserve;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.repository.ReserveRepository;
import com.project.volunteer.entity.Volunteer;
import com.project.volunteer.repository.VolunteerRepository;

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
@DisplayName("VolunteerRepository 테스트")
public class VolunteerRepositoryTest {

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private ReserveRepository reserveRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("봉사 일자와 시간대에 해당하는 총 예약 인원 수 조회 - 성공")
    void countByDateAndTime_성공() {
        // given - 회원 저장
        MemberEntity member1 = memberRepository.save(MemberEntity.builder()
                .memberName("이용찬")
                .memberId("voltest@test.com")
                .memberPw("1234")
                .memberPhone("01099998888")
                .memberAddress("서울시 송파구")
                .memberBirth(LocalDate.of(1990, 5, 5))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.MAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build());

        // 기준 일자/시간
        LocalDate targetDate = LocalDate.of(2025, 9, 10);
        String targetTime = "13:00 ~ 15:00";

        // 봉사 예약 1
        Reserve reserve1 = Reserve.builder()
                .member(member1)
                .applyDate(LocalDateTime.now())
                .reserveType(2) // 봉사 예약
                .reserveNumber(3) // 신청자 수
                .reserveState(ReserveState.ING)
                .note("봉사예약1")
                .build();
        reserve1 = reserveRepository.save(reserve1);

        Volunteer volunteer1 = Volunteer.builder()
                .volDate(targetDate)
                .volTime(targetTime)
                .reserve(reserve1)
                .build();
        volunteerRepository.save(volunteer1);
        
        MemberEntity member2 = memberRepository.save(MemberEntity.builder()
                .memberName("김찬")
                .memberId("voltest22@test.com")
                .memberPw("1234")
                .memberPhone("01056471234")
                .memberAddress("서울시 노원구")
                .memberBirth(LocalDate.of(1988, 7, 28))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.MAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build());

        // 봉사 예약 2
        Reserve reserve2 = Reserve.builder()
                .member(member2)
                .applyDate(LocalDateTime.now())
                .reserveType(2)
                .reserveNumber(2)
                .reserveState(ReserveState.ING)
                .note("봉사예약2")
                .build();
        reserve2 = reserveRepository.save(reserve2);

        Volunteer volunteer2 = Volunteer.builder()
                .volDate(targetDate)
                .volTime(targetTime)
                .reserve(reserve2)
                .build();
        volunteerRepository.save(volunteer2);

        // when
        Integer totalCount = volunteerRepository.countByDateAndTime(targetDate, targetTime);

        // then
        assertThat(totalCount).isEqualTo(5); // 3명 + 2명
    }
}