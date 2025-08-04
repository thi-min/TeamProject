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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ClosedDayServiceImpl implements ClosedDayService {

    private final ClosedDayRepository closedDayRepository;
    private final HolidayApiService holidayApiService;

    // 휴무일 등록 또는 수정
    @Override
    public void setClosedDay(ClosedDayRequestDto dto) {
        ClosedDay closedDay = ClosedDay.builder()
                .closedDate(dto.getClosedDate())
                .isClosed(dto.getIsClosed())
                .build();

        closedDayRepository.save(closedDay);
    }

    // 휴무일 삭제
    @Override
    public void deleteClosedDay(LocalDate date) {
        if (closedDayRepository.existsByClosedDate(date)) {
            closedDayRepository.deleteByClosedDate(date);
        }
    }

    // 해당 날짜가 휴무일인지 여부 확인
    @Override
    @Transactional(readOnly = true)
    public boolean isClosed(LocalDate date) {
        return closedDayRepository.findByClosedDate(date)
                .map(ClosedDay::getIsClosed)
                .orElse(false);
    }

    // 특정 기간 내 휴무일 전체 조회
    @Override
    @Transactional(readOnly = true)
    public List<ClosedDayResponseDto> getClosedDaysInPeriod(LocalDate start, LocalDate end) {
        return closedDayRepository.findByClosedDateBetween(start, end).stream()
                .map(cd -> new ClosedDayResponseDto(cd.getClosedDate(), cd.getIsClosed()))
                .collect(Collectors.toList());
    }
    
    // 명절 및 공휴일 자동등록
    @Override
    public void registerHolidays(int year) {
        List<LocalDate> autoClosedDates = new ArrayList<>();

        // 🔹 1. 공공 API를 통해 연도별 공휴일 가져오기
        List<LocalDate> holidaysFromApi = holidayApiService.getHolidays(year).stream()
                .filter(dto -> "Y".equals(dto.getIsHoliday()))
                .map(HolidayDto::getDate)
                .toList();

        autoClosedDates.addAll(holidaysFromApi);

        // 🔹 2. 수동 추가 항목
        autoClosedDates.add(LocalDate.of(year, 7, 12));  // 내 생일

        // 🔹 3. 중복 제거
        List<LocalDate> existingDates = closedDayRepository.findExistingDates(autoClosedDates);

        autoClosedDates.stream()
            .distinct()
            .filter(date -> !existingDates.contains(date))
            .forEach(date -> closedDayRepository.save(
                ClosedDay.builder()
                    .closedDate(date)
                    .isClosed(true)
                    .build()
            ));
    }
}
