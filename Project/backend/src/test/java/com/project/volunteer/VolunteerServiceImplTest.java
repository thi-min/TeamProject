package com.project.volunteer;

import com.project.common.entity.TimeSlot;
import com.project.member.entity.*;
import com.project.member.repository.MemberRepository;
import com.project.reserve.entity.Reserve;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.repository.ReserveRepository;
import com.project.volunteer.dto.VolunteerCountDto;
import com.project.volunteer.dto.VolunteerDetailDto;
import com.project.volunteer.entity.Volunteer;
import com.project.volunteer.repository.VolunteerRepository;
import com.project.volunteer.service.VolunteerService;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    
    @Autowired
    private EntityManager em;
    
    //@Test
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
                .build());

        // given - 기준 날짜 및 시간
        LocalDate targetDate = LocalDate.of(2025, 9, 15);
        TimeSlot timeSlot = TimeSlot.builder()
                .label("13:00 ~ 15:00")
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(15, 0))
                .build();
        em.persist(timeSlot);
        
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
                .timeSlot(timeSlot)
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
                .timeSlot(timeSlot)
                .reserve(reserve2)
                .build());

        // when - 서비스 호출
        VolunteerCountDto result = volunteerService.getVolunteerCountInfo(targetDate, timeSlot.getId());

        // then - 검증
        assertThat(result).isNotNull();
       
        assertThat(result.getLabel()).isEqualTo("13:00 ~ 15:00");
        assertThat(result.getReservedCount()).isEqualTo(5); // 2 + 3
        assertThat(result.getCapacity()).isEqualTo(10);
    }
    
    @Test
    @DisplayName("예약코드로 VolunteerDetailDto를 반환한다")
    void getVolunteerDetailByReserveCode_성공() {
        // given - 회원 생성
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .memberName("홍길동")
                .memberId("hong@test.com")
                .memberPw("1234")
                .memberPhone("01011112222")
                .memberAddress("서울시 강남구")
                .memberBirth(LocalDate.of(2005, 4, 23))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.WOMAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .build());
        
        TimeSlot timeSlot = TimeSlot.builder()
                .label("10:00 ~ 12:00")
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .build();
        em.persist(timeSlot);

        // 예약 생성
        Reserve reserve = reserveRepository.save(Reserve.builder()
                .member(member)
                .applyDate(LocalDateTime.now())
                .reserveType(2)
                .reserveNumber(1)
                .reserveState(ReserveState.ING)
                .note("봉사 예약 메모")
                .build());

        // 봉사 생성
        Volunteer volunteer = volunteerRepository.save(Volunteer.builder()
                .volDate(LocalDate.of(2025, 8, 10))
                .timeSlot(timeSlot)
                .reserve(reserve)
                .build());

        // when
        VolunteerDetailDto dto = volunteerService.getVolunteerDetailByReserveCode(reserve.getReserveCode());

        // then
        assertThat(dto.getMemberName()).isEqualTo("홍길동");
        assertThat(dto.getPhone()).isEqualTo("01011112222");
        assertThat(dto.getMemberBirth()).isEqualTo(LocalDate.of(2005, 4, 23));
        assertThat(dto.getReserveState()).isEqualTo(ReserveState.ING);
        assertThat(dto.getVolDate()).isEqualTo(LocalDate.of(2025, 8, 10));
        assertThat(dto.getLabel()).isEqualTo("10:00 ~ 12:00");
        assertThat(dto.getReserveNumber()).isEqualTo(1);
        assertThat(dto.getNote()).isEqualTo("봉사 예약 메모");
    }
}


