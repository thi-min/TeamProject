package com.project.common.service;

import com.project.common.dto.ClosedDayRequestDto;

import java.time.LocalDate;

public interface ClosedDayService {

    //휴무일 등록 또는 수정
    void setClosedDay(ClosedDayRequestDto dto);

    //휴무일 삭제
    void deleteClosedDay(LocalDate date);

    //해당 날짜가 휴무일인지 여부 확인
    boolean isClosed(LocalDate date);
}