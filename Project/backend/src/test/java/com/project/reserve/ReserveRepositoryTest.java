package com.project.reserve;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;

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
import com.project.volunteer.entity.Volunteer;
import com.project.volunteer.repository.VolunteerRepository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;


@SpringBootTest
@Transactional
@Rollback(false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReserveRepositoryTest {

    @Autowired
    private ReserveRepository reserveRepository;

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private LandRepository landRepository;
    
    @Autowired
    private VolunteerRepository volunteerRepository;
    
    @Autowired
    private EntityManager em;

    //@Test
    @DisplayName("회원번호로 예약 목록 조회")
    void testFindByMemberNum() {
        // given

    	MemberEntity member = MemberEntity.builder()
    			.memberId("test@test.com")
                .memberPw("1234")
                .memberName("홍길동")
                .memberBirth(LocalDate.of(1996, 1, 1))
                .memberPhone("01012345678")
                .memberAddress("서울시 강남구")
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.Man)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build();
        
        memberRepository.save(member);

        Reserve reserve = Reserve.builder()
                .member(member)
                .applyDate(LocalDateTime.now())
                .reserveType(1)
                .reserveNumber(2)
                .reserveState(ReserveState.ING)
                .note("테스트 예약")
                .build();
        
        reserveRepository.save(reserve); // 여기서 자동으로 reserveCode 생성됨!
        System.out.println("생성된 예약코드: " + reserve.getReserveCode());

        // when
        List<Reserve> result = reserveRepository.findByMember_MemberNum(member.getMemberNum());

        // then
        assertThat(result).isNotEmpty();	//예약이 있어야함
        assertThat(result.get(0).getMember().getMemberName()).isEqualTo("홍길동");
    }
    
    //@Test
    @DisplayName("회원번호 + 예약유형으로 예약 목록 조회")
    void testFindByMemberNumAndReserveType() {
        // given: 회원 및 예약 2건 (유형별로 1건씩) 저장
        MemberEntity member = MemberEntity.builder()
                .memberId("test5@test.com")
                .memberPw("7777")
                .memberName("김철수")
                .memberBirth(LocalDate.of(1995, 6, 5))
                .memberPhone("01088889999")
                .memberAddress("서울시 종로구")
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.Man)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build();

        memberRepository.save(member);

        // 놀이터 예약 (reserveType = 1)
        Reserve reserve1 = Reserve.builder()
                .member(member)
                .applyDate(LocalDateTime.now())
                .reserveType(1)
                .reserveNumber(3)
                .reserveState(ReserveState.ING)
                .note("놀이터 예약")
                .build();
        reserveRepository.save(reserve1);

        // 봉사 예약 (reserveType = 2)
        Reserve reserve2 = Reserve.builder()
                .member(member)
                .applyDate(LocalDateTime.now())
                .reserveType(2)
                .reserveNumber(1)
                .reserveState(ReserveState.ING)
                .note("봉사 예약")
                .build();
        reserveRepository.save(reserve2);

        // when: reserveType = 2 로 조회
        List<Reserve> result = reserveRepository.findByMember_MemberNumAndReserveType(member.getMemberNum(), 2);

        // then: 봉사 예약 1건만 조회되어야 함
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReserveType()).isEqualTo(2);
        assertThat(result.get(0).getNote()).isEqualTo("봉사 예약");
    }

    //@Test
    @DisplayName("놀이터 예약 조건 검색")
    void testSearchLandReservations() {
        // given: 회원 생성 및 저장
        MemberEntity member = MemberEntity.builder()
        		.memberId("love@test.com")
                .memberPw("4888")
                .memberName("강지원")
                .memberBirth(LocalDate.of(2001, 12, 10))
                .memberPhone("01098765432")
                .memberAddress("서울시 도봉구")
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.Woman)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build();
        
        memberRepository.save(member);

        // 놀이터 예약 생성
        Reserve reserve = Reserve.builder()
        		 .member(member)
                 .applyDate(LocalDateTime.now())
                 .reserveType(1)
                 .reserveNumber(1)
                 .reserveState(ReserveState.ING)
                 .note("")
                 .build();
        reserveRepository.save(reserve);
        
        Long reservecode = reserve.getReserveCode();

        Land land = Land.builder()
                .landDate(LocalDate.of(2025, 8, 1)) // 예약일
                .landTime("10:00 ~ 12:00")
                .landType(LandType.SMALL)
                .animalNumber(2)
                .payNumber(10000)
                .reserve(reserve)
                .build();
        
        land.setReserve(reserve);        
        reserve.setLandDetail(land);
        
        landRepository.save(land);

        // when
        List<Reserve> result = reserveRepository.searchLandReservations(
                reserve.getReserveCode(),	//예약코드
                "강지원",						//회원 이름으로 조회
                LocalDate.of(2025, 7, 31),	//조회시작일
                LocalDate.of(2025, 8, 2),	//조회종료일
                ReserveState.ING			//예약상태로 필터
        );

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getMember().getMemberName()).isEqualTo("강지원");
    }
    
    //@Test
    @DisplayName("봉사 예약 조건 검색")
    void testSearchVolunteerReservations() {
        // given - 회원 생성
        MemberEntity member = MemberEntity.builder()
                .memberId("volunteer@test.com")
                .memberPw("1234")
                .memberName("김봉사")
                .memberBirth(LocalDate.of(1998, 4, 20))
                .memberPhone("01011112222")
                .memberAddress("부산광역시")
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.Man)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build();
        memberRepository.save(member);

        // 예약 생성
        Reserve reserve = Reserve.builder()
                .member(member)
                .applyDate(LocalDateTime.now())
                .reserveType(2) // 봉사
                .reserveNumber(1)
                .reserveState(ReserveState.ING)
                .note("봉사 예약 테스트")
                .build();
        reserveRepository.save(reserve);
        
        Long reservecode = reserve.getReserveCode();

        // 봉사 상세 생성
        Volunteer volunteer = Volunteer.builder()
                .reserve(reserve)
                .volDate(LocalDate.of(2025, 8, 4))
                .volTime("13:00 ~ 15:00")
                .sumTime(null)
                .build();
        
        volunteer.setReserve(reserve);
        reserve.setVolunteerDetail(volunteer); // 양방향 연결
        
        volunteerRepository.save(volunteer);
        
        

        // when
        List<Reserve> result = reserveRepository.searchVolunteerReservations(
                reserve.getReserveCode(),
                "김봉사",
                LocalDate.of(2025, 7, 30),
                LocalDate.of(2025, 8, 5),
                ReserveState.ING
        );

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getMember().getMemberName()).isEqualTo("김봉사");
        assertThat(result.get(0).getVolunteerDetail()).isNotNull();
    }
    
    //@Test
    @DisplayName("관리자 - 예약 전체 목록 조회")
    void testFindAllWithDetails() {
        MemberEntity member = MemberEntity.builder()
                .memberId("an@test.com")
                .memberPw("1234")
                .memberName("안지만")
                .memberBirth(LocalDate.of(1990, 7, 7))
                .memberPhone("01012345678")
                .memberAddress("서울시 강남구")
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.Man)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build();
        memberRepository.save(member);

        // given: 예약 생성 (reserveType 1 = 놀이터)
        Reserve reserve1 = Reserve.builder()
                .member(member)
                .applyDate(LocalDateTime.now())
                .reserveType(1)
                .reserveNumber(1)
                .reserveState(ReserveState.ING)
                .note("놀이터 예약")
                .build();
        reserveRepository.save(reserve1);

        // land 연결
        Land land = Land.builder()
                .landDate(LocalDate.of(2025, 8, 2))
                .landTime("13:00 ~ 15:00")
                .landType(LandType.LARGE)
                .animalNumber(1)
                .payNumber(15000)
                .build();
        land.setReserve(reserve1);
        reserve1.setLandDetail(land);
        landRepository.save(land);

        // given: 예약 생성 (reserveType 2 = 봉사)
        Reserve reserve2 = Reserve.builder()
                .member(member)
                .applyDate(LocalDateTime.now())
                .reserveType(2)
                .reserveNumber(1)
                .reserveState(ReserveState.ING)
                .note("봉사 예약")
                .build();
        reserveRepository.save(reserve2);

        // volunteer 연결
        Volunteer volunteer = Volunteer.builder()
                .volDate(LocalDate.of(2025, 8, 3))
                .volTime("09:00 ~ 12:00")
                .sumTime(null) // null 허용
                .build();
        volunteer.setReserve(reserve2);
        reserve2.setVolunteerDetail(volunteer);
        volunteerRepository.save(volunteer);

        // when
        List<Reserve> result = reserveRepository.findAllWithDetails();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isGreaterThanOrEqualTo(2);

        for (Reserve r : result) {
            System.out.println("예약번호: " + r.getReserveCode());
            System.out.println("회원명: " + r.getMember().getMemberName());
            System.out.println("유형: " + (r.getReserveType() == 1 ? "놀이터" : "봉사"));
            if (r.getLandDetail() != null) {
                System.out.println("놀이터 일자: " + r.getLandDetail().getLandDate());
            }
            if (r.getVolunteerDetail() != null) {
                System.out.println("봉사 일자: " + r.getVolunteerDetail().getVolDate());
            }
        }
    }
    

    //@Test
    @DisplayName("놀이터 예약 중복 확인 테스트")
    void existsByMemberAndLandDateTime() {
        // given
        MemberEntity member = MemberEntity.builder()
                .memberId("kim@test.com")
                .memberPw("1234")
                .memberName("김진열")
                .memberBirth(LocalDate.of(1990, 7, 7))
                .memberPhone("01012345555")
                .memberAddress("서울시 강남구")
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.Man) // <-- enum 이름 대문자 확인
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build();
        em.persist(member);;

        Reserve reserve = Reserve.builder()
                .member(member)
                .reserveType(1)
                .reserveState(ReserveState.ING)
                .applyDate(LocalDateTime.now())
                .reserveNumber(3)
                .note("중복 테스트")
                .build();
        em.persist(reserve);

        Land land = Land.builder()
                .reserve(reserve)
                .landDate(LocalDate.of(2025, 8, 10))
                .landTime("11:00 ~ 13:00")
                .landType(null)
                .animalNumber(2)
                .payNumber(10000)
                .build();
        em.persist(land);

        em.flush();
        em.clear();

        // when
        boolean exists = reserveRepository.existsByMember_MemberNumAndLandDetail_LandDateAndLandDetail_LandTime(
                member.getMemberNum(),
                LocalDate.of(2025, 8, 10),
                "11:00 ~ 13:00"
        );

        // then
        assertThat(exists).isTrue();
    }
    
    @Test
    @DisplayName("같은 회원이 같은 시간에 예약하면 중복 true 나오는지 확인")
    void testDuplicateReservationDetection() {
        // given
    	MemberEntity member = MemberEntity.builder()
                .memberId("yang@test.com")
                .memberPw("1234")
                .memberName("양현모")
                .memberBirth(LocalDate.of(1990, 7, 7))
                .memberPhone("01012345555")
                .memberAddress("서울시 강남구")
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.Man) // <-- enum 이름 대문자 확인
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build();
        em.persist(member);;

        // 첫 번째 예약
        Reserve firstReserve = Reserve.builder()
                .member(member)
                .reserveType(1)
                .reserveState(ReserveState.ING)
                .applyDate(LocalDateTime.now())
                .reserveNumber(1)
                .note("첫 예약")
                .build();
        em.persist(firstReserve);

        Land firstLand = Land.builder()
                .reserve(firstReserve)
                .landDate(LocalDate.of(2025, 8, 10))
                .landTime("11:00 ~ 13:00")
                .landType(LandType.SMALL)
                .animalNumber(1)
                .payNumber(10000)
                .build();
        firstReserve.setLandDetail(firstLand);
        em.persist(firstLand);

        // 두 번째 예약 (같은 시간)
        Reserve secondReserve = Reserve.builder()
                .member(member)
                .reserveType(1)
                .reserveState(ReserveState.ING)
                .applyDate(LocalDateTime.now())
                .reserveNumber(1)
                .note("중복 예약")
                .build();
        em.persist(secondReserve);

        Land secondLand = Land.builder()
                .reserve(secondReserve)
                .landDate(LocalDate.of(2025, 8, 10)) // 같은 날짜
                .landTime("11:00 ~ 13:00")           // 같은 시간
                .landType(LandType.SMALL)
                .animalNumber(1)
                .payNumber(10000)
                .build();
        secondReserve.setLandDetail(secondLand);
        em.persist(secondLand);

        em.flush();
        em.clear();

        // when: 중복 검사
        boolean exists = reserveRepository.existsByMember_MemberNumAndLandDetail_LandDateAndLandDetail_LandTime(
                member.getMemberNum(),
                LocalDate.of(2025, 8, 10),
                "11:00 ~ 13:00"
        );

        // then: 중복이므로 true여야 함
        System.out.println("중복 여부: " + exists);
        assertThat(exists).isTrue();
    }
    
    
}