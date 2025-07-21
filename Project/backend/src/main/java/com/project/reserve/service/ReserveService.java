package com.project.reserve.service;

import com.project.reserve.dto.ReserveRequestDto;
import com.project.reserve.dto.ReserveResponseDto;
import com.project.reserve.entity.ReserveState;

import java.util.Date;
import java.util.List;

public interface ReserveService {

     Long createReserve(ReserveRequestDto requestDto);

	 List<ReserveResponseDto> getReservesByMember(Long memberNum);

	 ReserveResponseDto getReserveByCode(Long reserveCode);

	 List<ReserveResponseDto> getReservesByDate(Date date);

	 List<ReserveResponseDto> getReservesByType(int type);

	 void updateReserveState(Long reserveCode, ReserveState newState);

	 void memberCancelReserve(Long reserveCode, Long memberNum);
}