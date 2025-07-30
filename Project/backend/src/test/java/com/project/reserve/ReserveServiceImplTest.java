package com.project.reserve;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.project.land.dto.LandDetailDto;
import com.project.land.dto.LandRequestDto;
import com.project.land.entity.Land;
import com.project.land.entity.LandType;
import com.project.land.repository.LandRepository;
import com.project.land.service.LandService;
import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberSex;
import com.project.member.entity.MemberState;
import com.project.member.repository.MemberRepository;
import com.project.reserve.dto.AdminReservationListDto;
import com.project.reserve.dto.AdminReservationSearchDto;
import com.project.reserve.dto.FullReserveRequestDto;
import com.project.reserve.dto.ReserveCompleteResponseDto;
import com.project.reserve.dto.ReserveRequestDto;
import com.project.reserve.dto.ReserveResponseDto;
import com.project.reserve.entity.Reserve;
import com.project.reserve.entity.ReserveState;
import com.project.reserve.exception.DuplicateReservationException;
import com.project.reserve.repository.ReserveRepository;
import com.project.reserve.service.ReserveServiceImpl;
import com.project.volunteer.entity.Volunteer;
import com.project.volunteer.repository.VolunteerRepository;
import com.project.volunteer.service.VolunteerService;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
@DisplayName("ReserveServiceImpl 통합 테스트")
class ReserveServiceImplTest {

    @Autowired
    private ReserveServiceImpl reserveService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReserveRepository reserveRepository;
    
    @Autowired
    private LandRepository landRepository;
    
    @Autowired
    private VolunteerRepository volunteerRepository;
    
    @Autowired
    private EntityManager entityManager;

    private Long testMemberNum;
   

    //@Test
    @DisplayName("중복된 놀이터 예약이 존재할 경우 예외 발생")
    void createReserve_shouldThrow_whenDuplicateLandReservationExists() {
        // given: 첫 번째 예약 선 저장 (중복 예약 조건 유도)
        Reserve reserve = Reserve.builder()
                .member(memberRepository.findById(testMemberNum).orElseThrow())
                .reserveType(1)
                .reserveState(ReserveState.ING)
                .applyDate(LocalDateTime.now())
                .reserveNumber(1)
                .note("선행 예약")
                .build();
        reserveRepository.save(reserve);

        Land land = Land.builder()
                .reserve(reserve)
                .landDate(LocalDate.of(2025, 8, 10))
                .landTime("11:00 ~ 13:00")
                .landType(LandType.SMALL)
                .animalNumber(1)
                .payNumber(10000)
                .build();
        
        landRepository.save(land);
        reserve.setLandDetail(land); // 양방향 연결
         
        entityManager.flush();
        entityManager.clear();
        // reserveCode를 공유하는 land는 save 생략 가능하지만 확실히 하려면 직접 save
        // landRepository.save(land); 필요 시 추가

        // when: 같은 시간에 예약 시도
        ReserveRequestDto reserveDto = ReserveRequestDto.builder()
                .memberNum(testMemberNum)
                .reserveType(1)
                .reserveNumber(1)
                .note("선행 예약")
                .build();

        LandRequestDto landDto = LandRequestDto.builder()
                .landDate(LocalDate.of(2025, 8, 10))
                .landTime("11:00 ~ 13:00")
                .landType(LandType.SMALL)
                .animalNumber(1)
                .payNumber(10000)
                .build();
      

        FullReserveRequestDto fullDto = FullReserveRequestDto.builder()
                .reserveDto(reserveDto)
                .landDto(landDto)
                .build();

        // then: 중복 예외 발생
        assertThatThrownBy(() -> reserveService.createReserve(fullDto))
        .isInstanceOf(DuplicateReservationException.class)
        .hasMessageContaining("이미 해당 시간에 놀이터 예약이 존재합니다.");
    }
    
    @Transactional
    @Test
    @DisplayName("정상적인 놀이터 예약 생성")
    void createLandReservation_success() {
        // given
    	MemberEntity member = memberRepository.save(MemberEntity.builder()
                .memberName("김도영")
                .memberId("tigers@test.com")
                .memberPw("1212")
                .memberPhone("01065478912")
                .memberAddress("광주시 북구")
                .memberBirth(LocalDate.of(2004, 10, 2))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.MAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build());
    	Long testMemberNum = member.getMemberNum();
    	
        ReserveRequestDto reserveDto = ReserveRequestDto.builder()
                .memberNum(testMemberNum)
                .reserveType(1)  // 놀이터 예약
                .reserveNumber(1)
                .note("기아 화이팅")
                .build();

        LandRequestDto landDto = LandRequestDto.builder()
                .landDate(LocalDate.of(2025, 8, 10))
                .landTime("11:00 ~ 13:00")
                .landType(LandType.SMALL)
                .animalNumber(1)
                .payNumber(10000)
                .build();

        FullReserveRequestDto fullDto = FullReserveRequestDto.builder()
                .reserveDto(reserveDto)
                .landDto(landDto)
                .build();

        // when
        ReserveCompleteResponseDto responseDto = reserveService.createReserve(fullDto);
        Long reserveCode = responseDto.getReserveCode();

        // then
        assertThat(responseDto.getMessage()).isEqualTo("놀이터 예약이 완료되었습니다.");
        Optional<Reserve> savedReserveOpt = reserveRepository.findById(reserveCode);
        assertThat(savedReserveOpt).isPresent(); //예약이 저장되었음을 검증

        Reserve savedReserve = savedReserveOpt.get();
        assertThat(savedReserve.getMember().getMemberNum()).isEqualTo(testMemberNum);	//해당 사용자의 예약이 맞는지 검증
        assertThat(savedReserve.getReserveType()).isEqualTo(1);	//놀이터 예약인지
        assertThat(savedReserve.getReserveNumber()).isEqualTo(1);	//예약 인원이 1명으로 저장되었는지
        assertThat(savedReserve.getNote()).isEqualTo("기아 화이팅");

        Land land = savedReserve.getLandDetail();
        assertThat(land).isNotNull();
        assertThat(land.getLandDate()).isEqualTo(LocalDate.of(2025, 8, 10));
        assertThat(land.getLandTime()).isEqualTo("11:00 ~ 13:00");
        assertThat(land.getLandType()).isEqualTo(LandType.SMALL);
    }
    
    //@Test
    @Rollback(false)
    @DisplayName("봉사 예약 시 volunteerDto 누락 시 예외 발생")
    void createVolunteerReservation_shouldThrow_whenVolunteerDtoIsNull() {
        // given
        ReserveRequestDto reserveDto = ReserveRequestDto.builder()
                .memberNum(testMemberNum)
                .reserveType(2)  // 봉사 예약
                .reserveNumber(1)
                .note("봉사 예약 테스트")
                .build();

        FullReserveRequestDto fullDto = FullReserveRequestDto.builder()
                .reserveDto(reserveDto)
                .volunteerDto(null)  // 누락된 봉사 DTO
                .build();

        // when & then
        assertThatThrownBy(() -> reserveService.createReserve(fullDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("봉사 예약 세부 정보가 누락되었습니다.");
    }
    
    //@Test
    @Transactional
    @DisplayName("존재하는 회원의 예약 목록 정상 조회")
    void getReservesByMember_success() {
    

        // 예약 데이터 추가
        Reserve reserve = Reserve.builder()
                .member(memberRepository.findById(testMemberNum).orElseThrow())
                .reserveType(1)
                .reserveNumber(2)
                .note("집가고싶어")
                .applyDate(LocalDateTime.now())
                .reserveState(ReserveState.ING)
                .build();
        reserveRepository.save(reserve);
       
        // when
        List<ReserveResponseDto> result = reserveService.getReservesByMember(testMemberNum);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getReserveType()).isEqualTo(1);
        assertThat(result.get(0).getReserveState()).isEqualTo(ReserveState.ING);
    }
    
    //@Test
    @DisplayName("관리자가 전체 예약 목록을 정상적으로 조회한다")
    void getAllReservationsForAdmin_success() {
    	
    	LocalDate expectedDate = LocalDate.of(2025,  8, 10);
        // given - 회원과 예약 데이터 생성
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .memberName("임꺽정")
                .memberId("im@test.com")
                .memberPw("1234")
                .memberPhone("01045155555")
                .memberAddress("서울시 관악구")
                .memberBirth(LocalDate.of(1999, 1, 1))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.MAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build());

        Reserve reserve = reserveRepository.save(Reserve.builder()
                .member(member)
                .reserveType(1)
                .reserveNumber(1)
                .note("관리자 테스트 예약")
                .applyDate(LocalDateTime.now())
                .reserveState(ReserveState.ING)
                .build());
        reserveRepository.save(reserve);
        
        Land land = Land.builder()
                .reserve(reserve)
                .landDate(expectedDate)
                .landTime("11:00 ~ 13:00")
                .landType(LandType.SMALL)
                .animalNumber(1)
                .payNumber(1)
                .build();
        landRepository.save(land);
        
        reserve.setLandDetail(land);
        // when
        List<AdminReservationListDto> result = reserveService.getAllReservationsForAdmin();

        // then
        assertThat(result).isNotEmpty();
        
        AdminReservationListDto dto = result.get(0);
        assertThat(result.get(0).getReserveCode()).isEqualTo(reserve.getReserveCode());
        assertThat(result.get(0).getReserveState()).isEqualTo(ReserveState.ING.name());
        assertThat(dto.getReserveDate()).isEqualTo(expectedDate);
    }
    //@Test
    @DisplayName("본인의 놀이터 예약 상세 정보를 정상적으로 조회한다")
    void getMemberLandReserveDetail_success() {
        // given
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .memberName("감스트")
                .memberId("gamst@test.com")
                .memberPw("1234")
                .memberPhone("01032334545")
                .memberAddress("서울시 노원구")
                .memberBirth(LocalDate.of(2000, 4, 11))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.MAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build());
        
        Reserve reserve = reserveRepository.save(Reserve.builder()
                .member(member)
                .reserveType(1)
                .reserveNumber(1)
                .note("놀이터 예약")
                .applyDate(LocalDateTime.now())
                .reserveState(ReserveState.ING)
                .build());

        Land land = Land.builder()
                .reserve(reserve)
                .landDate(LocalDate.of(2025, 8, 10))
                .landTime("11:00 ~ 13:00")
                .landType(LandType.SMALL)
                .animalNumber(1)
                .payNumber(10000)
                .build();
        landRepository.save(land);
        reserve.setLandDetail(land);

        // when
        LandDetailDto result = reserveService.getMemberLandReserveDetail(reserve.getReserveCode(), member.getMemberNum());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getReserveCode()).isEqualTo(reserve.getReserveCode());
        assertThat(result.getMemberName()).isEqualTo(member.getMemberName());
        assertThat(result.getLandDate()).isEqualTo(LocalDate.of(2025, 8, 10));
    }
    //@Test
    @DisplayName("사용자가 자신의 예약을 정상적으로 취소한다")
    void memberCancelReserve_success() {
        // given
    	MemberEntity member = memberRepository.findById(testMemberNum)
    	        .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
    	 Reserve reserve = reserveRepository.save(Reserve.builder()
    	            .member(member)
    	            .reserveType(1)
    	            .reserveNumber(1)
    	            .note("테스트 예약")
    	            .applyDate(LocalDateTime.now())
    	            .reserveState(ReserveState.ING)
    	            .build());

        // when
        reserveService.memberCancelReserve(reserve.getReserveCode(), member.getMemberNum());

        // then
        Reserve updated = reserveRepository.findById(reserve.getReserveCode())
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));
        assertThat(updated.getReserveState()).isEqualTo(ReserveState.CANCEL);
    }
    //@Test
    @DisplayName("다른 사용자가 예약을 취소하려고 하면 예외가 발생한다")
    void memberCancelReserve_invalidUser_throwsSecurityException() {
        //예약자 본인
    	MemberEntity owner = memberRepository.save(
    	        MemberEntity.builder()
    	            .memberId("kiatigers@test.com")
    	            .memberPw("5555")
    	            .memberName("김도영")
    	            .memberPhone("01011112222")
    	            .memberAddress("광주광역시")
    	            .memberBirth(LocalDate.of(2003, 10, 2))
    	            .memberDay(LocalDate.now())
    	            .memberSex(MemberSex.MAN)
    	            .memberState(MemberState.ACTIVE)
    	            .memberLock(false)
    	            .snsYn(false)
    	            .build()
    	    );
    	//타인
    	MemberEntity stranger = memberRepository.save(
    	        MemberEntity.builder()
    	            .memberId("lgtwins@test.com")
    	            .memberPw("1234")
    	            .memberName("홍창기")
    	            .memberPhone("01022223333")
    	            .memberAddress("서울시 강남구")
    	            .memberBirth(LocalDate.of(1993, 11, 21))
    	            .memberDay(LocalDate.now())
    	            .memberSex(MemberSex.MAN)
    	            .memberState(MemberState.ACTIVE)
    	            .memberLock(false)
    	            .snsYn(false)
    	            .build()
    	    );
    	Reserve reserve = reserveRepository.save(
    	        Reserve.builder()
    	            .member(owner)
    	            .reserveType(1)
    	            .reserveNumber(1)
    	            .note("테스트 예약")
    	            .applyDate(LocalDateTime.now())
    	            .reserveState(ReserveState.ING)
    	            .build()
    	    );
        // when & then
        assertThatThrownBy(() -> 
            reserveService.memberCancelReserve(reserve.getReserveCode(), stranger.getMemberNum())
        ).isInstanceOf(SecurityException.class)
         .hasMessageContaining("본인의 예약만 취소할 수 있습니다.");
    }
    //@Test
    @DisplayName("이미 취소된 예약을 다시 취소하려고 하면 예외가 발생한다")
    void memberCancelReserve_alreadyCancelled_throwsIllegalStateException() {
        // given
    	MemberEntity member = memberRepository.findById(testMemberNum)
    		        .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
    	Reserve reserve = reserveRepository.save(
    	        Reserve.builder()
    	            .member(member)
    	            .reserveType(1)
    	            .reserveNumber(1)
    	            .note("취소 테스트 예약")
    	            .applyDate(LocalDateTime.now())
    	            .reserveState(ReserveState.CANCEL) // 이미 취소된 상태
    	            .build()
    	    );

        // when & then
        assertThatThrownBy(() ->
            reserveService.memberCancelReserve(reserve.getReserveCode(), member.getMemberNum())
        ).isInstanceOf(IllegalStateException.class)
         .hasMessageContaining("이미 취소된 예약입니다.");
    }

    //@Test
    @DisplayName("관리자 - 놀이터 예약 조건 검색 성공")
    void searchLandReservationsForAdmin_withValidFilter_returnsCorrectResults() {
        // given
    	MemberEntity member = memberRepository.save(MemberEntity.builder()
                .memberName("이재현")
                .memberId("lions@test.com")
                .memberPw("7777")
                .memberPhone("01086339413")
                .memberAddress("대구광역시")
                .memberBirth(LocalDate.of(2003, 2, 4))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.MAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build());

        // 예약 1 - 조건에 맞는 예약
        Reserve matchingReserve = reserveRepository.save(
            Reserve.builder()
                .member(member)
                .reserveNumber(1)
                .reserveType(1) // 놀이터
                .applyDate(LocalDateTime.of(2025, 7, 1, 10, 0))
                .note("테스트 예약 1")
                .reserveState(ReserveState.ING)
                .build()
        );
        Land land = Land.builder()
                .reserve(matchingReserve)
                .landDate(LocalDate.of(2025, 7, 1)) // 검색에 걸릴 날짜
                .landTime("10:00 ~ 12:00")
                .landType(LandType.SMALL)
                .animalNumber(1)
                .payNumber(10000)
                .build();
        
        matchingReserve.setLandDetail(land);
        landRepository.save(land);
        
        // 조건에 맞지 않는 봉사 예약
        reserveRepository.save(
            Reserve.builder()
                .member(member)
                .reserveNumber(2)
                .reserveType(2) // 봉사
                .applyDate(LocalDateTime.of(2025, 7, 1, 10, 0))
                .note("테스트 예약 2")
                .reserveState(ReserveState.ING)
                .build()
        );
        // 조건에 맞지 않는 날짜의 놀이터 예약
        Reserve wrongDateReserve = reserveRepository.save(
            Reserve.builder()
                .member(member)
                .reserveNumber(1)
                .reserveType(1)
                .applyDate(LocalDateTime.of(2024, 1, 1, 10, 0))
                .note("테스트 예약 3")
                .reserveState(ReserveState.REJ)
                .build()
        );
        
        Land wrongDateLand = Land.builder()
                .reserve(wrongDateReserve)
                .landDate(LocalDate.of(2024, 1, 1))
                .landTime("10:00 ~ 12:00")
                .landType(LandType.SMALL)
                .animalNumber(1)
                .payNumber(8000)
                .build();

        wrongDateReserve.setLandDetail(wrongDateLand);
        landRepository.save(wrongDateLand);
        

        AdminReservationSearchDto searchDto = AdminReservationSearchDto.builder()
            .reserveCode(null)
            .memberName(member.getMemberName())
            .startDate(LocalDate.of(2025, 6, 30))
            .endDate(LocalDate.of(2025, 7, 31))
            .reserveState(ReserveState.ING)
            .build();

        // when
        List<AdminReservationListDto> result = reserveService.searchLandReservationsForAdmin(searchDto);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReserveDate()).isEqualTo(LocalDate.of(2025, 7, 1));
        assertThat(result.get(0).getMemberName()).isEqualTo(member.getMemberName());
        assertThat(result.get(0).getReserveState()).isEqualTo(ReserveState.ING);
    }
    
    //@Test
    @DisplayName("관리자 - 봉사 예약 조건 검색 성공")
    void searchVolunteerReservationsForAdmin_withValidFilter_returnsCorrectResults() {
        // given: 테스트 회원 등록
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .memberName("유봉사")
                .memberId("volunteer@test.com")
                .memberPw("1234")
                .memberPhone("01012345678")
                .memberAddress("서울시 마포구")
                .memberBirth(LocalDate.of(1990, 5, 20))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.MAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build());

        // 조건에 맞는 봉사 예약 생성
        Reserve matchingReserve = reserveRepository.save(Reserve.builder()
                .member(member)
                .reserveType(2) // 봉사
                .reserveNumber(1)
                .reserveState(ReserveState.ING)
                .applyDate(LocalDateTime.of(2025, 7, 15, 10, 0))
                .note("조건 일치 봉사 예약")
                .build());

        Volunteer volunteer = Volunteer.builder()
                .reserve(matchingReserve)
                .volDate(LocalDate.of(2025, 8, 1))
                .volTime("09:00 ~ 12:00")
                .build();
        matchingReserve.setVolunteerDetail(volunteer);
        volunteerRepository.save(volunteer);

        // 조건에 맞지 않는 다른 유형의 예약 생성
        reserveRepository.save(Reserve.builder()
                .member(member)
                .reserveType(1) // 놀이터
                .reserveNumber(1)
                .reserveState(ReserveState.ING)
                .applyDate(LocalDateTime.of(2025, 7, 15, 10, 0))
                .note("조건 불일치 놀이터 예약")
                .build());

        // 검색 DTO 설정
        AdminReservationSearchDto searchDto = AdminReservationSearchDto.builder()
                .reserveCode(null)
                .memberName("유봉사")
                .startDate(LocalDate.of(2025, 7, 31))
                .endDate(LocalDate.of(2025, 8, 2))
                .reserveState(ReserveState.ING)
                .build();

        // when: 검색 실행
        List<AdminReservationListDto> result = reserveService.searchVolunteerReservationsForAdmin(searchDto);

        // then: 검색 결과 검증
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMemberName()).isEqualTo("유봉사");
        assertThat(result.get(0).getReserveDate()).isEqualTo(LocalDate.of(2025, 8, 1));
        assertThat(result.get(0).getReserveState()).isEqualTo(ReserveState.ING);
    }
    //@Test
    @DisplayName("관리자 - 예약 상태 변경 성공")
    void updateReserveStateByAdmin_updatesSuccessfully() {
        // given
        MemberEntity member = memberRepository.save(
            MemberEntity.builder()
                .memberName("양의지")
                .memberId("doosan@test.com")
                .memberPw("1234")
                .memberPhone("01022223333")
                .memberAddress("서울")
                .memberBirth(LocalDate.of(1987, 6, 5))
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.MAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build()
        );

        // 예약 생성
        Reserve reserve = reserveRepository.save(
            Reserve.builder()
                .member(member)
                .reserveType(1)
                .reserveNumber(1)
                .note("상태 변경 테스트")
                .applyDate(LocalDateTime.now())
                .reserveState(ReserveState.ING)
                .build()
        );

        // when
        reserveService.updateReserveStateByAdmin(reserve.getReserveCode(), ReserveState.DONE);

        // then
        Reserve updated = reserveRepository.findById(reserve.getReserveCode())
            .orElseThrow(() -> new IllegalArgumentException("예약 없음"));
        assertThat(updated.getReserveState()).isEqualTo(ReserveState.DONE);
    }
    //@Test
    @DisplayName("회원 예약을 예약유형별로 조회하면 해당 유형의 예약만 반환된다")
    void getReservesByMemberAndType_returnsFilteredReservations() {
        // given
        MemberEntity member = memberRepository.save(MemberEntity.builder()
            .memberId("why@test.com")
            .memberPw("1111")
            .memberName("박명수")
            .memberPhone("01012345678")
            .memberAddress("서울시")
            .memberBirth(LocalDate.of(1995, 5, 5))
            .memberDay(LocalDate.now())
            .memberSex(MemberSex.MAN)
            .memberState(MemberState.ACTIVE)
            .memberLock(false)
            .snsYn(false)
            .build());

        // 놀이터 예약 (type = 1)
        reserveRepository.save(Reserve.builder()
            .member(member)
            .reserveType(1)
            .reserveNumber(1)
            .note("놀이터 예약")
            .applyDate(LocalDateTime.now())
            .reserveState(ReserveState.ING)
            .build());

        // 봉사 예약 (type = 2)
        reserveRepository.save(Reserve.builder()
            .member(member)
            .reserveType(2)
            .reserveNumber(2)
            .note("봉사 예약")
            .applyDate(LocalDateTime.now())
            .reserveState(ReserveState.ING)
            .build());

        // when
        List<ReserveResponseDto> landResults = reserveService.getReservesByMemberAndType(member.getMemberNum(), 1);
        List<ReserveResponseDto> volunteerResults = reserveService.getReservesByMemberAndType(member.getMemberNum(), 2);

        // then
        assertThat(landResults).hasSize(1);
        assertThat(landResults.get(0).getReserveType()).isEqualTo(1);

        assertThat(volunteerResults).hasSize(1);
        assertThat(volunteerResults.get(0).getReserveType()).isEqualTo(2);
    }
    
    
}

