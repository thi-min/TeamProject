package com.project.timeslot;

import com.project.common.dto.TimeSlotDto;
import com.project.common.entity.TimeSlot;
import com.project.common.entity.TimeType;
import com.project.common.repository.TimeSlotRepository;
import com.project.common.service.TimeSlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import jakarta.transaction.Transactional;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Rollback(false)
@DisplayName("TimeSlotServiceImpl 테스트")
class TimeSlotServiceImplTest {

    @Autowired
    private TimeSlotService timeSlotService;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    private TimeSlotDto baseDto;

    //@BeforeEach
    void setUp() {
        baseDto = TimeSlotDto.builder()
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(12, 0))
                .enabled(true)
                .capacity(10)
                .timeType(TimeType.VOL) // ✅ 추가
                .build();
    }

    @Test
    @DisplayName("시간대 추가 - 성공")
    void addTimeSlot_성공() {
        TimeSlotDto dto = TimeSlotDto.builder()
                .startTime(LocalTime.of(13, 0))
                .endTime(LocalTime.of(16, 0))
                .enabled(true)
                .capacity(10)
                .timeType(TimeType.VOL) // ✅ 추가
                .build();

        timeSlotService.addTimeSlot(dto);

        TimeSlot saved = timeSlotRepository.findAll().stream()
                .filter(ts -> ts.getLabel().equals("13:00 ~ 16:00"))
                .findFirst()
                .orElseThrow();

        assertThat(saved.getCapacity()).isEqualTo(dto.getCapacity());
        assertThat(saved.getTimeType()).isEqualTo(TimeType.VOL);
    }

    //@Test
    @DisplayName("startTime과 endTime이 같으면 예외 발생")
    void addTimeSlot_같은시간_예외() {
        TimeSlotDto dto = TimeSlotDto.builder()
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(10, 0))
                .enabled(true)
                .capacity(10)
                .timeType(TimeType.LAND) // ✅ 추가
                .build();

        assertThatThrownBy(() -> timeSlotService.addTimeSlot(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시작 시간은 종료 시간보다 이전이어야 합니다.");
    }

    //@Test
    @DisplayName("startTime이 endTime보다 늦으면 예외 발생")
    void addTimeSlot_역순시간_예외() {
        TimeSlotDto dto = TimeSlotDto.builder()
                .startTime(LocalTime.of(15, 0))
                .endTime(LocalTime.of(14, 0))
                .enabled(true)
                .capacity(10)
                .timeType(TimeType.LAND) // ✅ 추가
                .build();

        assertThatThrownBy(() -> timeSlotService.addTimeSlot(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시작 시간은 종료 시간보다 이전이어야 합니다.");
    }

    //@Test
    @DisplayName("시간대 중복 여부 확인 - 존재할 경우 true")
    void isDuplicateLabel_존재함() {
        TimeSlotDto dto = TimeSlotDto.builder()
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .enabled(true)
                .capacity(10)
                .timeType(TimeType.LAND) // ✅ 추가
                .build();

        timeSlotService.addTimeSlot(dto);

        boolean result = timeSlotService.isDuplicateLabel("10:00 ~ 12:00");
        assertThat(result).isTrue();
    }

    //@Test
    @DisplayName("시간대 수정 - 성공")
    void updateTimeSlot_성공() {
        timeSlotService.addTimeSlot(baseDto);
        TimeSlot saved = timeSlotRepository.findAll().stream()
                .filter(ts -> ts.getLabel().equals("12:00 ~ 14:00"))
                .findFirst()
                .orElseThrow();

        TimeSlotDto updatedDto = TimeSlotDto.builder()
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .enabled(false)
                .capacity(8)
                .timeType(TimeType.LAND) // ✅ 추가
                .build();

        timeSlotService.updateTimeSlot(saved.getId(), updatedDto);

        TimeSlot updated = timeSlotRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getStartTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(updated.getEndTime()).isEqualTo(LocalTime.of(12, 0));
        assertThat(updated.getLabel()).isEqualTo("10:00 ~ 12:00");
        assertThat(updated.isEnabled()).isFalse();
        assertThat(updated.getTimeType()).isEqualTo(TimeType.LAND);
    }

    //@Test
    @DisplayName("존재하지 않는 시간대 수정 시 예외 발생")
    void updateTimeSlot_예외() {
        TimeSlotDto dto = TimeSlotDto.builder()
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .enabled(true)
                .capacity(10)
                .timeType(TimeType.LAND) // ✅ 추가
                .build();

        assertThatThrownBy(() -> timeSlotService.updateTimeSlot(999L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 시간대가 존재하지 않습니다.");
    }

    //@Test
    @DisplayName("존재하지 않는 시간대 삭제 시 예외 발생")
    void deleteTimeSlot_예외() {
        assertThatThrownBy(() -> timeSlotService.deleteTimeSlot(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 시간대입니다.");
    }

    //@Test
    @DisplayName("정원이 0명 이하일 경우 예외 발생")
    void addTimeSlot_capacity_음수_예외() {
        TimeSlotDto dto = TimeSlotDto.builder()
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .enabled(true)
                .capacity(0)
                .timeType(TimeType.LAND) // ✅ 추가
                .build();

        assertThatThrownBy(() -> timeSlotService.addTimeSlot(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("정원은 1명 이상");
    }
}