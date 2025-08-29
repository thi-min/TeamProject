package com.project.closedday;

import com.project.common.entity.ClosedDay;
import com.project.common.repository.ClosedDayRepository;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
@DisplayName("ClosedDayRepository 테스트")
public class ClosedDayRepositoryTest {

    @Autowired
    ClosedDayRepository closedDayRepository;

    //@Test
    @DisplayName("휴무일 저장 및 조회 테스트")
    void testSaveAndFindByClosedDate() {
        LocalDate date = LocalDate.of(2025, 8, 15);
        ClosedDay closedDay = ClosedDay.builder()
                .closedDate(date)
                .isClosed(true)
                .build();

        closedDayRepository.save(closedDay);

        Optional<ClosedDay> found = closedDayRepository.findByClosedDate(date);
        assertThat(found).isPresent();
        assertThat(found.get().getClosedDate()).isEqualTo(date);
        assertThat(found.get().getIsClosed()).isTrue();
    }

    //@Test
    @DisplayName("휴무일 존재 여부 확인")
    void testExistsByClosedDate() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        closedDayRepository.save(ClosedDay.builder().closedDate(date).isClosed(true).build());

        boolean exists = closedDayRepository.existsByClosedDate(date);
        assertThat(exists).isTrue();
    }

    //@Test
    @DisplayName("특정 기간 내 휴무일 조회")
    void testFindByClosedDateBetween() {
        LocalDate d1 = LocalDate.of(2025, 1, 1);
        LocalDate d2 = LocalDate.of(2025, 1, 15);
        LocalDate d3 = LocalDate.of(2025, 2, 1);

        closedDayRepository.save(ClosedDay.builder().closedDate(d1).isClosed(true).build());
        closedDayRepository.save(ClosedDay.builder().closedDate(d2).isClosed(true).build());
        closedDayRepository.save(ClosedDay.builder().closedDate(d3).isClosed(true).build());

        List<ClosedDay> results = closedDayRepository.findByClosedDateBetween(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31)
        );

        assertThat(results).hasSize(2);
    }

    //@Test
    @DisplayName("중복 날짜 조회 쿼리 테스트")
    void testFindExistingDates() {
        LocalDate d1 = LocalDate.of(2025, 12, 25);
        LocalDate d2 = LocalDate.of(2025, 1, 1);

        closedDayRepository.save(ClosedDay.builder().closedDate(d1).isClosed(true).build());

        List<LocalDate> input = List.of(d1, d2);
        List<LocalDate> existing = closedDayRepository.findExistingDates(input);

        assertThat(existing).containsExactly(d1);
    }

    @Test
    @DisplayName("휴무일 삭제 테스트")
    void testDeleteByClosedDate() {
        LocalDate date = LocalDate.of(2025, 8, 15);
        closedDayRepository.save(ClosedDay.builder().closedDate(date).isClosed(true).build());

        closedDayRepository.deleteByClosedDate(date);

        boolean exists = closedDayRepository.existsByClosedDate(date);
        assertThat(exists).isFalse();
    }
}