package com.project.closedday;

import com.project.common.dto.ClosedDayRequestDto;
import com.project.common.dto.ClosedDayResponseDto;
import com.project.common.dto.HolidayDto;
import com.project.common.entity.ClosedDay;
import com.project.common.repository.ClosedDayRepository;
import com.project.common.service.ClosedDayService;
import com.project.common.service.HolidayApiService;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
@DisplayName("공휴일 API 호출 단위 테스트")
class ClosedDayServiceImplTest {

    @Autowired
    HolidayApiService holidayApiService;
    
    @Autowired
    private ClosedDayService closedDayService;

    @Autowired
    private ClosedDayRepository closedDayRepository;
    
    //@Test
    void testCallHolidayApi() {
        // given
        int testYear = 2025;

        // when
        List<HolidayDto> holidays = holidayApiService.getHolidays(testYear);

        // then
        assertThat(holidays).isNotNull();
        assertThat(holidays).isNotEmpty();

        holidays.forEach(dto -> {
            System.out.println("공휴일 이름: " + dto.getName());
            System.out.println("날짜: " + dto.getDate());
            System.out.println("isHoliday: " + dto.getIsHoliday());
            System.out.println("------");
        });
    }
    
    //@Test
    @DisplayName("휴무일 등록 또는 수정 테스트")
    void testSetClosedDay() {
        // given
        LocalDate testDate = LocalDate.of(2025, 8, 15);
        ClosedDayRequestDto dto = new ClosedDayRequestDto(testDate, true);

        // when
        closedDayService.setClosedDay(dto);

        // then
        Optional<ClosedDay> saved = closedDayRepository.findByClosedDate(testDate);
        assertThat(saved).isPresent();
        assertThat(saved.get().getIsClosed()).isTrue ();
    }
    
    //@Test
    @DisplayName("휴무일 삭제 테스트")
    void testDeleteClosedDay() {
        // given
        LocalDate testDate = LocalDate.of(2025, 8, 15);
        closedDayRepository.save(ClosedDay.builder().closedDate(testDate).isClosed(true).build());

        // when
        closedDayService.deleteClosedDay(testDate);

        // then
        assertThat(closedDayRepository.existsByClosedDate(testDate)).isFalse();
    }
    
    //@Test
    @DisplayName("특정 날짜 휴무일 여부 확인 테스트")
    void testIsClosed() {
        // given
        LocalDate testDate = LocalDate.of(2025, 1, 1);
        closedDayRepository.save(ClosedDay.builder().closedDate(testDate).isClosed(true).build());

        // when
        boolean result = closedDayService.isClosed(testDate);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("기간 내 휴무일 전체 조회 테스트")
    void testGetClosedDaysInPeriod() {
        // given
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);
        closedDayRepository.save(ClosedDay.builder().closedDate(LocalDate.of(2025, 1, 1)).isClosed(true).build());
        closedDayRepository.save(ClosedDay.builder().closedDate(LocalDate.of(2025, 5, 5)).isClosed(true).build());

        // when
        List<ClosedDayResponseDto> result = closedDayService.getClosedDaysInPeriod(start, end);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).extracting("closedDate").contains(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 5, 5));
    }
}
