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

import com.project.land.entity.Land;
import com.project.land.entity.LandType;
import com.project.common.entity.TimeSlot;
import com.project.land.dto.LandCountDto;
import com.project.land.dto.LandDetailDto;
import com.project.land.repository.LandRepository;
import com.project.land.service.LandService;
import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberSex;
import com.project.member.entity.MemberState;
import com.project.member.repository.MemberRepository;
import com.project.reserve.entity.Reserve;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.repository.ReserveRepository;
import com.project.land.dto.LandCountDto;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
@DisplayName("LandServiceImpl 테스트")
class LandServiceImplTest {
	
	@Autowired
    private LandRepository landRepository;

    @Autowired
    private ReserveRepository reserveRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LandService landService;
    
    @Autowired
    private EntityManager em;
    
    //@Test
    @DisplayName("소형견 놀이터 예약 마릿수 조회 - 성공")
    void getLandCountInfo_SMALL_성공() {
		// 1. 회원 생성 및 저장
	    MemberEntity member = memberRepository.save(MemberEntity.builder()
	            .memberName("강민호")
	            .memberId("mino@test.com")
	            .memberPw("4747")
	            .memberPhone("01054550566")
	            .memberAddress("부산시 진구")
	            .memberBirth(LocalDate.of(1995, 5, 5))
	            .memberDay(LocalDate.now())
	            .memberSex(MemberSex.MAN)
	            .memberState(MemberState.ACTIVE)
	            .memberLock(false)
	            .snsYn(false)
	            .build());
	
	    // 2. 기준 날짜, 시간대, 타입
	    LocalDate date = LocalDate.of(2025, 9, 10);
	    String time = "09:00 ~ 11:00";
	    LandType type = LandType.SMALL;
	    
	    // 시간 설정
	    TimeSlot timeSlot = TimeSlot.builder()
	            .label("09:00 ~ 11:00")
	            .startTime(LocalTime.of(9, 0))
	            .endTime(LocalTime.of(11, 0))
	            .build();
	    em.persist(timeSlot);
	    
	    // 3. 예약 및 Land 저장 (3마리)
	    Reserve reserve = Reserve.builder()
	            .member(member)
	            .applyDate(LocalDateTime.now())
	            .reserveType(1)
	            .reserveNumber(2)
	            .reserveState(ReserveState.ING)
	            .note("소형견 예약")
	            .build();
	
	    Land land = Land.builder()
	            .landDate(date)
	            .timeSlot(timeSlot)
	            .landType(type)
	            .animalNumber(3)
	            .payNumber(12000)
	            .build();
	
	    reserve.setLandDetail(land);
	    land.setReserve(reserve);
	    reserveRepository.save(reserve);
	
	    // 4. 서비스 호출
	    LandCountDto result = landService.getLandCountInfo(date, timeSlot.getId(), type);
	
	    // 5. 검증
	    assertThat(result).isNotNull();
	    assertThat(result.getLandDate()).isEqualTo(date);
	    assertThat(result.getLabel()).isEqualTo("09:00 ~ 11:00");
	    assertThat(result.getLandType()).isEqualTo(type);
	    assertThat(result.getReservedCount()).isEqualTo(3);
	    assertThat(result.getCapacity()).isEqualTo(15);
	}
    
    @Test
    @DisplayName("예약코드로 LandDetailDto를 반환하고 결제 계산이 정확한지 확인한다")
    void getLandDetailByReserveCode_성공_및_금액계산_확인() {
        // given - 회원 저장
    	MemberEntity member = memberRepository.save(MemberEntity.builder()
	            .memberName("손아섭")
	            .memberId("ah@test.com")
	            .memberPw("7729")
	            .memberPhone("01099448888")
	            .memberAddress("부산시 사상구")
	            .memberBirth(LocalDate.of(1998, 7, 9))
	            .memberDay(LocalDate.now())
	            .memberSex(MemberSex.MAN)
	            .memberState(MemberState.ACTIVE)
	            .memberLock(false)
	            .snsYn(false)
	            .build());
    	// 시간
    	TimeSlot timeSlot = TimeSlot.builder()
	            .label("09:00 ~ 11:00")
	            .startTime(LocalTime.of(9, 0))
	            .endTime(LocalTime.of(11, 0))
	            .build();
	    em.persist(timeSlot);

        // given - 예약 저장
        Reserve reserve = Reserve.builder()
                .member(member)
                .applyDate(LocalDateTime.now())
                .reserveType(1)
                .reserveNumber(3)  // 보호자 수
                .reserveState(ReserveState.ING)
                .note("테스트 예약")
                .build();
        
        reserve = reserveRepository.save(reserve);
        
        int expectedBasePrice = 2000;
        int expectedAdditional = (2 - 1) * 1000 + 3 * 1000;
        int expectedTotal = expectedBasePrice + expectedAdditional;
        
        // given - 놀이터 정보 저장
        Land land = Land.builder()
                .reserve(reserve)
                .landDate(LocalDate.now().plusDays(1))
                .timeSlot(timeSlot)
                .landType(LandType.SMALL)
                .animalNumber(2)  // 반려견 2마리
                .payNumber(expectedTotal)
                .build();
        
        
        // 양방향 관계 설정
        reserve.setLandDetail(land);
        land.setReserve(reserve);
 
        
        landRepository.save(land); 
        // when
        LandDetailDto result = landService.getLandDetailByReserveCode(reserve.getReserveCode());

        // then - 데이터 필드 검증
        assertThat(result.getMemberName()).isEqualTo("손아섭");
        assertThat(result.getPhone()).isEqualTo("01099448888");
        assertThat(result.getLandType()).isEqualTo(LandType.SMALL);
        assertThat(result.getAnimalNumber()).isEqualTo(2);
        assertThat(result.getReserveNumber()).isEqualTo(3);
        assertThat(result.getTotalPrice()).isEqualTo(land.getPayNumber());
        // then - 결제 금액 계산 검증
        assertThat(result.getBasePrice()).isEqualTo(expectedBasePrice);
        assertThat(result.getAdditionalPrice()).isEqualTo(expectedAdditional);
        assertThat(result.getTotalPrice()).isEqualTo(expectedTotal);
    }
}
