package com.project.fund.service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.fund.dto.FundRequestDto;
import com.project.fund.dto.FundResponseDto;
import com.project.fund.entity.FundEntity;
import com.project.fund.repository.FundRepository;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FundServiceImpl implements FundService {

    private final FundRepository fundRepository;
    private final MemberRepository memberRepository;

    // FundEntity를 FundResponseDto로 변환하는 수동 메서드
    private FundResponseDto toDto(FundEntity entity) {
        if (entity == null) {
            return null;
        }

        return FundResponseDto.builder()
                .fundId(entity.getFundId())
                .memberId(entity.getMember() != null ? entity.getMember().getMemberNum() : null)
                .fundSponsor(entity.getFundSponsor())
                .fundPhone(entity.getFundPhone())
                .fundBirth(entity.getFundBirth())
                .fundType(entity.getFundType())
                .fundMoney(entity.getFundMoney())
                .fundTime(entity.getFundTime())
                .fundItem(entity.getFundItem())
                .fundNote(entity.getFundNote())
                .fundBank(entity.getFundBank())
                .fundAccountNum(entity.getFundAccountNum())
                .fundDepositor(entity.getFundDepositor())
                .fundDrawlDate(entity.getFundDrawlDate())
                .fundCheck(entity.getFundCheck())
                .build();
    }

    // 후원신청서 생성( if memberid가 없을시 예외처리
    @Override
    public FundResponseDto createFund(FundRequestDto dto) {
        MemberEntity member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + dto.getMemberId()));
        
        FundEntity entity = FundEntity.builder()
                .member(member)
                .fundSponsor(dto.getFundSponsor())
                .fundPhone(dto.getFundPhone())
                .fundBirth(dto.getFundBirth())
                .fundType(dto.getFundType())
                .fundMoney(dto.getFundMoney())
                .fundTime(LocalDateTime.now())
                .fundItem(dto.getFundItem())
                .fundNote(dto.getFundNote())
                .fundBank(dto.getFundBank())
                .fundAccountNum(dto.getFundAccountNum())
                .fundDepositor(dto.getFundDepositor())
                .fundDrawlDate(dto.getFundDrawlDate())
                .fundCheck(dto.getFundCheck())
                .build();

        FundEntity created = fundRepository.save(entity);
        return toDto(created);
    }

    // 특정 id 후원정보 조회(회원)
    @Override
    @Transactional(readOnly = true)
    public FundResponseDto getFund(Long id) {
        FundEntity entity = fundRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Fund not found with id: " + id));
        return toDto(entity);
    }
    
    // 모든 후원 정보 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public Page<FundResponseDto> getFunds(Pageable pageable) {
        return fundRepository.findAll(pageable).map(this::toDto);
    }
    
    // 후원자 명으로 후원정보 검색
    @Override
    @Transactional(readOnly = true)
    public Page<FundResponseDto> searchBySponsor(String sponsor, Pageable pageable) {
        return fundRepository.findByFundSponsorContaining(sponsor, pageable).map(this::toDto);
    }
    // 특정 id에 해당하는 후원정보 수정 갱신
    @Override
    public FundResponseDto updateFund(Long id, FundRequestDto dto) {
        FundEntity exist = fundRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Fund not found with id: " + id));

        // DTO 필드를 기존 엔티티에 업데이트
        exist.setFundSponsor(dto.getFundSponsor());
        exist.setFundPhone(dto.getFundPhone());
        exist.setFundBirth(dto.getFundBirth());
        exist.setFundType(dto.getFundType());
        exist.setFundMoney(dto.getFundMoney());
        exist.setFundTime(dto.getFundTime());
        exist.setFundItem(dto.getFundItem());
        exist.setFundNote(dto.getFundNote());
        exist.setFundBank(dto.getFundBank());
        exist.setFundAccountNum(dto.getFundAccountNum());
        exist.setFundDepositor(dto.getFundDepositor());
        exist.setFundDrawlDate(dto.getFundDrawlDate());
        exist.setFundCheck(dto.getFundCheck());

        FundEntity updated = fundRepository.save(exist);
        return toDto(updated);
    }
    // 후원정보 삭제
    @Override
    public void deleteFund(Long id) {
        fundRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FundResponseDto> listByMemberNum(Long memberNum, Pageable pageable) {
        return fundRepository.findByMember_MemberNum(memberNum, pageable) // MemberNum 기준으로 조회
                             .map(this::toDto); // Entity → DTO 변환
    }
}