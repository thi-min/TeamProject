package com.project.fund.service;

import com.project.fund.dto.FundRequestDto;
import com.project.fund.dto.FundResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FundService {

    FundResponseDto createFund(FundRequestDto dto);

    FundResponseDto getFund(Long fundId);

    Page<FundResponseDto> getFunds(Pageable pageable);

    Page<FundResponseDto> searchBySponsor(String sponsor, Pageable pageable);

    FundResponseDto updateFund(Long fundId, FundRequestDto dto);

    void deleteFund(Long fundId);
}