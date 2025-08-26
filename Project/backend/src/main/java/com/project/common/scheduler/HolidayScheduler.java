package com.project.common.scheduler;

import com.project.common.service.ClosedDayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class HolidayScheduler {

    private final ClosedDayService closedDayService;

    // 매분 실행 (테스트용)
    @Scheduled(cron = "0 0 1 1 12 *")
    public void registerNextYearHolidays() {
        int nextYear = LocalDate.now().plusYears(1).getYear();
        log.info("자동 실행: {}년 공휴일 등록 시작", nextYear);
        closedDayService.registerHolidays(nextYear);
    }
}