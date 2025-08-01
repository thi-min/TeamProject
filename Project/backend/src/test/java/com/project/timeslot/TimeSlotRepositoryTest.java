package com.project.timeslot;

import com.project.common.entity.TimeSlot;
import com.project.common.repository.TimeSlotRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import jakarta.transaction.Transactional;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
@DisplayName("TimeSlotRepository 테스트")
class TimeSlotRepositoryTest {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    //@Test
    @DisplayName("label 중복 확인 - 존재하는 경우 true 반환")
    void existsByLabel_존재함() {
        // given
        String label = "09:00 ~ 11:00";
        TimeSlot timeSlot = TimeSlot.builder()
                .label(label)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .enabled(true)
                .build();
        timeSlotRepository.save(timeSlot);

        // when
        boolean exists = timeSlotRepository.existsByLabel(label);

        // then
        assertThat(exists).isTrue();
    }

    //@Test
    @DisplayName("label 중복 확인 - 존재하지 않으면 false 반환")
    void existsByLabel_존재하지않음() {
        // given
        String label = "13:00 ~ 15:00";

        // when
        boolean exists = timeSlotRepository.existsByLabel(label);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("startTime 기준 오름차순 정렬 확인")
    void findAllByOrderByStartTimeAsc_정렬확인() {
        // given
        TimeSlot slot1 = TimeSlot.builder()
                .label("15:00 ~ 17:00")
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(17, 0))
                .enabled(true)
                .build();

        TimeSlot slot2 = TimeSlot.builder()
                .label("08:00 ~ 10:00")
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .build();

        TimeSlot slot3 = TimeSlot.builder()
                .label("12:00 ~ 14:00")
                .startTime(LocalTime.of(12, 0))
                .endTime(LocalTime.of(14, 0))
                .build();

        timeSlotRepository.save(slot1);
        timeSlotRepository.save(slot2);
        timeSlotRepository.save(slot3);

        // when
        List<TimeSlot> sortedList = timeSlotRepository.findAllByOrderByStartTimeAsc();

        // then
        assertThat(sortedList).isNotEmpty();
        assertThat(sortedList.get(0).getStartTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(sortedList.get(1).getStartTime()).isEqualTo(LocalTime.of(12, 0));
        assertThat(sortedList.get(2).getStartTime()).isEqualTo(LocalTime.of(15, 0));
    }
}