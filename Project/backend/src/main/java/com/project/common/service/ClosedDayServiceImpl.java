package com.project.common.service;

import com.project.common.dto.ClosedDayRequestDto;
import com.project.common.dto.ClosedDayResponseDto;
import com.project.common.dto.HolidayDto;
import com.project.common.entity.ClosedDay;
import com.project.common.repository.ClosedDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClosedDayServiceImpl implements ClosedDayService {

    private final ClosedDayRepository closedDayRepository;
    private final HolidayApiService holidayApiService;
    
    // 휴무일 등록/수정
    @Override
    public void setClosedDay(ClosedDayRequestDto dto) {
        ClosedDay closedDay = ClosedDay.builder()
                .closedDate(dto.getClosedDate())
                .reason(dto.getReason()) 
                .isClosed(dto.getIsClosed())
                .build();

        closedDayRepository.save(closedDay);
    }

    // 휴무일 삭제
    @Override
    public void deleteClosedDay(LocalDate date) {
        closedDayRepository.deleteByClosedDate(date);
    }

    // 특정 날짜 휴무 여부
    @Override
    @Transactional(readOnly = true)
    public boolean isClosed(LocalDate date) {
        return closedDayRepository.existsByClosedDate(date);
    }

    // 특정 기간 내 휴무일 조회
    @Override
    @Transactional(readOnly = true)
    public List<ClosedDayResponseDto> getClosedDaysInPeriod(LocalDate start, LocalDate end) {
        return closedDayRepository.findByClosedDateBetween(start, end)
                .stream()
                .map(cd -> ClosedDayResponseDto.builder()
                        .closedDate(cd.getClosedDate())
                        .reason(cd.getReason())
                        .isClosed(cd.getIsClosed())
                        .build())
                .collect(Collectors.toList());
    }

    // 공휴일 자동 등록 (HolidayApiService 사용 가능)
    @Override
    public void registerHolidays(int year) {
        // HolidayApiService 통해 공휴일 가져오기
        List<HolidayDto> holidays = holidayApiService.getHolidays(year);

        for (HolidayDto holiday : holidays) {
            ClosedDay closedDay = ClosedDay.builder()
                    .closedDate(holiday.getDate())
                    .reason(holiday.getName())
                    .isClosed("Y".equalsIgnoreCase(holiday.getIsHoliday()))
                    .build();

            // ✅ 중복이면 holidayName 업데이트, 없으면 새로 저장
            closedDayRepository.findById(closedDay.getClosedDate())
                    .ifPresentOrElse(
                        existing -> {
                            existing.setReason(holiday.getName());
                            existing.setIsClosed(closedDay.getIsClosed()); // 필요하다면 isClosed도 갱신
                            closedDayRepository.save(existing);
                        },
                        () -> closedDayRepository.save(closedDay)
                    );
        }
    }
    
    @Override
    public List<ClosedDayResponseDto> getClosedDays(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return getClosedDaysInPeriod(start, end); // 범용 메서드 재사용
    }
}