package com.project.common.service;

import com.project.common.dto.ClosedDayRequestDto;
import com.project.common.dto.ClosedDayResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ClosedDayService {

    //휴무일 등록 또는 수정
    void setClosedDay(ClosedDayRequestDto dto);

    //휴무일 삭제
    void deleteClosedDay(LocalDate date);

    //해당 날짜가 휴무일인지 여부 확인
    public boolean isClosed(LocalDate date);
    
    //특정 기간 내 휴무일 전체 조회
    List<ClosedDayResponseDto> getClosedDaysInPeriod(LocalDate start, LocalDate end);
    
    //명절이나 크리스마스 자동 등록
    void registerHolidays(int year);
    
    //특정 연/월의 휴무일 조회 (달력 UI에서 한 달 표시용)
    List<ClosedDayResponseDto> getClosedDays(int year, int month);
}