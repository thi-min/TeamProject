package com.project.land;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.project.common.entity.TimeSlot;
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

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
@DisplayName("LandRepository 테스트")
class LandRepositoryTest {

    @Autowired
    private LandRepository landRepository;

    @Autowired
    private ReserveRepository reserveRepository;

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private EntityManager em;
    
    //@Test
    @DisplayName("예약코드로 land 조회 - 성공")
    void findByReserveCode_성공() {
        // 1. 회원 저장
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .memberName("김영수")
                .memberId("lt1234@test.com")
                .memberPw("1234")
                .memberPhone("01011112222")
                .memberAddress("서울시 관악구")
                .memberBirth(LocalDate.of(2000, 9, 30))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.MAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build());
        
        TimeSlot timeSlot = TimeSlot.builder()
                .label("09:00 ~ 11:00")
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .build();
        em.persist(timeSlot);
        
        // 2. 예약 저장
        Reserve reserve = Reserve.builder()
        	    .member(member)
        	    .applyDate(LocalDateTime.now())
        	    .reserveType(1)
        	    .reserveNumber(2)
        	    .reserveState(ReserveState.ING)
        	    .note("land 테스트")
        	    .build();
        
        // 3. land 저장
        Land land = Land.builder()
        	    .landDate(LocalDate.of(2025, 8, 15))
        	    .timeSlot(timeSlot)
        	    .landType(LandType.SMALL)
        	    .animalNumber(1)
        	    .payNumber(10000)
        	    .build();
        
        // 양방형 설정
        reserve.setLandDetail(land);
        land.setReserve(reserve);
        
        reserveRepository.save(reserve);

        // 4. 검증
        Optional<Land> found = landRepository.findByReserveCode(reserve.getReserveCode());

        assertThat(found).isPresent();
        assertThat(found.get().getLandDate()).isEqualTo(LocalDate.of(2025, 8, 15));
        assertThat(found.get().getTimeSlot().getLabel()).isEqualTo("09:00 ~ 11:00");
        assertThat(found.get().getLandType()).isEqualTo(LandType.SMALL);
        assertThat(found.get().getAnimalNumber()).isEqualTo(1);
    }
    
    //@Test
    @DisplayName("LandType으로 land 조회 - 성공")
    void findByLandType_성공() {
        // 1. 회원 저장
    	MemberEntity member = memberRepository.save(MemberEntity.builder()
                .memberName("구자욱")
                .memberId("lions@test.com")
                .memberPw("5555")
                .memberPhone("01098871445")
                .memberAddress("대구시 수성구")
                .memberBirth(LocalDate.of(1999, 3, 15))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.MAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build());
    	
    	TimeSlot timeSlot1 = TimeSlot.builder()
                .label("10:00 ~ 12:00")
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))       
                .build();
        em.persist(timeSlot1);
        
        // 2. 소형견 놀이터 예약
        Reserve reserve1 = Reserve.builder()
                .member(member)
                .applyDate(LocalDateTime.now())
                .reserveType(1)
                .reserveNumber(2)
                .reserveState(ReserveState.ING)
                .note("소형견놀이터")
                .build();

        Land land1 = Land.builder()
                .landDate(LocalDate.of(2025, 9, 1))
                .timeSlot(timeSlot1)
                .landType(LandType.SMALL)
                .animalNumber(2)
                .payNumber(15000)
                .build();
        reserve1.setLandDetail(land1);
        land1.setReserve(reserve1);
        reserveRepository.save(reserve1);
        
        //3. 대형견 놀이터 예약
        TimeSlot timeSlot2 = TimeSlot.builder()
                .label("14:00 ~ 16:00")
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))       
                .build();
        em.persist(timeSlot2);
        
        Reserve reserve2 = Reserve.builder()
                .member(member)
                .applyDate(LocalDateTime.now())
                .reserveType(1)
                .reserveNumber(3)
                .reserveState(ReserveState.ING)
                .note("대형견놀이터")
                .build();

        Land land2 = Land.builder()
                .landDate(LocalDate.of(2025, 9, 2))
                .timeSlot(timeSlot2)
                .landType(LandType.LARGE)
                .animalNumber(1)
                .payNumber(12000)
                .build();

        reserve2.setLandDetail(land2);
        land2.setReserve(reserve2);
        reserveRepository.save(reserve2);
        
        // 3. 조회 및 검증
        List<Land> result = landRepository.findByLandType(LandType.SMALL);

        assertThat(result).hasSize(1);
        assertThat(result).allMatch(land -> land.getLandType() == LandType.SMALL);
        assertThat(result.get(0).getAnimalNumber()).isEqualTo(2);
        assertThat(result.get(0).getPayNumber()).isEqualTo(15000);
        }   
    
    @Test
    @DisplayName("특정 날짜, 시간대, LandType에 해당하는 반려견 수 합산 - 성공")
    void countByDateAndTimeAndType_성공() {
        // 1. 회원 저장
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .memberName("강호동")
                .memberId("kang@test.com")
                .memberPw("8888")
                .memberPhone("01033334444")
                .memberAddress("대전시 유성구")
                .memberBirth(LocalDate.of(2000, 7, 20))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.MAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build());

        // 2. 기준 날짜 및 시간
        LocalDate targetDate = LocalDate.of(2025, 9, 5);
        TimeSlot timeSlot = TimeSlot.builder()
                .label("10:00 ~ 12:00")
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .build();
        em.persist(timeSlot);

        // 3. 소형견 예약 (3마리)
        Reserve reserve1 = Reserve.builder()
                .member(member)
                .applyDate(LocalDateTime.now())
                .reserveType(1)
                .reserveNumber(2)
                .reserveState(ReserveState.ING)
                .note("소형견 예약")
                .build();

        Land land1 = Land.builder()
                .landDate(targetDate)
                .timeSlot(timeSlot)
                .landType(LandType.SMALL)
                .animalNumber(3)		//3마리
                .payNumber(15000)
                .build();
        reserve1.setLandDetail(land1);
        land1.setReserve(reserve1);
        reserveRepository.save(reserve1);

        // 4. 대형견 예약 (2마리)
        Reserve reserve2 = Reserve.builder()
                .member(member)
                .applyDate(LocalDateTime.now())
                .reserveType(1)
                .reserveNumber(1)
                .reserveState(ReserveState.ING)
                .note("대형견 예약")
                .build();

        Land land2 = Land.builder()
                .landDate(targetDate)
                .timeSlot(timeSlot)
                .landType(LandType.LARGE)
                .animalNumber(2)		//2마리
                .payNumber(12000)
                .build();
        
        
        reserve2.setLandDetail(land2);
        land2.setReserve(reserve2);
        reserveRepository.save(reserve2);
        
        // 소형견예약 추가
        Reserve reserve3 = Reserve.builder()
                .member(member)
                .applyDate(LocalDateTime.now())
                .reserveType(1)
                .reserveNumber(1)
                .reserveState(ReserveState.ING)
                .note("소형견 예약2")
                .build();

        Land land3 = Land.builder()
                .landDate(targetDate)
                .timeSlot(timeSlot)
                .landType(LandType.SMALL)
                .animalNumber(1)	//1마리
                .payNumber(12000)
                .build();
        
        reserve3.setLandDetail(land3);
        land3.setReserve(reserve3);
        reserveRepository.save(reserve3);

        // 5. 쿼리 테스트 (소형견만)
        Integer result = landRepository.countByDateAndTimeAndType(targetDate, timeSlot, LandType.SMALL);

        // 6. 검증: 소형견 예약만 합산되어야 하므로 3이어야 함
        assertThat(result).isEqualTo(4);
    }
}    
    

