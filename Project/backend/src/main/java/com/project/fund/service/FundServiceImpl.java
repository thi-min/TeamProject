package com.project.fund.service;

import java.time.LocalDateTime;

import com.project.fund.dto.FundRequestDto;
import com.project.fund.dto.FundResponseDto;
import com.project.fund.entity.FundEntity;
import com.project.fund.mapper.FundMapper;
import com.project.fund.repository.FundRepository;
import com.project.fund.exception.ResourceNotFoundException;
import com.project.member.entity.MemberEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FundServiceImpl implements FundService {

    private final FundRepository fundRepository;
    private final FundMapper fundMapper;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public FundServiceImpl(FundRepository fundRepository, FundMapper fundMapper) {
        this.fundRepository = fundRepository;
        this.fundMapper = fundMapper;
    }

    @Override
    public FundResponseDto createFund(FundRequestDto dto) {
        FundEntity e = fundMapper.toEntity(dto);

        // member 레퍼런스만 설정 (DB에서 실제 조회 없이 proxy 생성)
        MemberEntity memberRef = em.getReference(MemberEntity.class, dto.getMemberId());
        e.setMember(memberRef);

        // fundTime 기본값 설정: 요청에 값이 없으면 현재 시간
        if (e.getFundTime() == null) {
            e.setFundTime(LocalDateTime.now());
        }

        FundEntity saved = fundRepository.save(e);
        return fundMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FundResponseDto getFund(Long fundId) {
        FundEntity e = fundRepository.findById(fundId)
                .orElseThrow(() -> new ResourceNotFoundException("Fund not found with id: " + fundId));
        return fundMapper.toDto(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FundResponseDto> getFunds(Pageable pageable) {
        return fundRepository.findAll(pageable).map(fundMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FundResponseDto> searchBySponsor(String sponsor, Pageable pageable) {
        return fundRepository.findByFundSponsorContainingIgnoreCase(sponsor, pageable).map(fundMapper::toDto);
    }

    @Override
    public FundResponseDto updateFund(Long fundId, FundRequestDto dto) {
        FundEntity existing = fundRepository.findById(fundId)
                .orElseThrow(() -> new ResourceNotFoundException("Fund not found with id: " + fundId));

        // member 변경이 필요한 경우 member 레퍼런스 재설정
        if (dto.getMemberId() != null) {
            MemberEntity memberRef = em.getReference(MemberEntity.class, dto.getMemberId());
            existing.setMember(memberRef);
        }

        fundMapper.updateEntityFromDto(dto, existing);

        FundEntity saved = fundRepository.save(existing);
        return fundMapper.toDto(saved);
    }

    @Override
    public void deleteFund(Long fundId) {
        if (!fundRepository.existsById(fundId)) {
            throw new ResourceNotFoundException("Fund not found with id: " + fundId);
        }
        fundRepository.deleteById(fundId);
    }
}