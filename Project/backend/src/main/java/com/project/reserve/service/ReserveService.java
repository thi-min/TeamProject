package com.project.reserve.service;

import com.project.reserve.dto.ReserveRequestDto;
import com.project.reserve.dto.ReserveResponseDto;
import com.project.reserve.entity.ReserveState;

import java.util.List;

public interface ReserveService {

    //Long createReserve(ReserveRequestDto requestDto);

    ReserveResponseDto getReserveByCode(Long reserveCode);

    List<ReserveResponseDto> getReservesByMember(Long memberNum);

    void cancelReserve(Long reserveCode);

    void updateReserveState(Long reserveCode, ReserveState newState);
}