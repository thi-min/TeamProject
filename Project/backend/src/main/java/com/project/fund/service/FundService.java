package com.project.fund.service;

import com.project.fund.dto.FundRequestDto;
import com.project.fund.dto.FundResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FundService {
	// 후원신청서 생성
    FundResponseDto createFund(FundRequestDto dto);
    // 후원신청서 저장
    FundResponseDto getFund(Long fundId);
    // 후원 신청서 조회
    Page<FundResponseDto> getFunds(Pageable pageable);
    // 특정 후원자 조회
    Page<FundResponseDto> searchBySponsor(String sponsor, Pageable pageable);
    // 후원서 갱신
    FundResponseDto updateFund(Long fundId, FundRequestDto dto);
    // 후원내역 삭제
    void deleteFund(Long fundId);
}