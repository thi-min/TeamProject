package com.project.fund.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.fund.dto.FundRequestDto;
import com.project.fund.dto.FundResponseDto;
import com.project.fund.entity.FundEntity;
import com.project.fund.service.FundService;
import com.project.member.entity.MemberEntity;

import jakarta.validation.Valid;

/**
 * Fund API Controller
 *
 * 기본 경로: /api/funds
 */
@RestController
@RequestMapping("/api/funds")
public class FundController {

    private final FundService fundService;

    @Autowired
    public FundController(FundService fundService) {
        this.fundService = fundService;
    }

    /**
     * DTO를 Entity로 변환하는 메서드
     * @param dto FundRequestDto
     * @return FundEntity
     */
    private FundEntity toEntity(FundRequestDto dto) {
        if (dto == null) {
            return null;
        }

        FundEntity.FundEntityBuilder builder = FundEntity.builder()
                .fundSponsor(dto.getFundSponsor())
                .fundPhone(dto.getFundPhone())
                .fundBirth(dto.getFundBirth())
                .fundType(dto.getFundType())
                .fundMoney(dto.getFundMoney())
                .fundTime(dto.getFundTime())
                .fundItem(dto.getFundItem())
                .fundNote(dto.getFundNote())
                .fundBank(dto.getFundBank())
                .fundAccountNum(dto.getFundAccountNum())
                .fundDepositor(dto.getFundDepositor())
                .fundDrawlDate(dto.getFundDrawlDate())
                .fundCheck(dto.getFundCheck());

        // MemberEntity 생성자 오류를 해결하기 위해 MemberEntity를 명확하게 설정
        if (dto.getMemberId() != null) {
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setMemberNum(dto.getMemberId());
            builder.member(memberEntity);
        }

        return builder.build();
    }

    /**
     * Entity를 DTO로 변환하는 메서드
     * @param entity FundEntity
     * @return FundResponseDto
     */
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


    // 생성
    @PostMapping
    public ResponseEntity<FundResponseDto> createFund(@Valid @RequestBody FundRequestDto dto) {
        FundResponseDto created = fundService.createFund(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<FundResponseDto> getFund(@PathVariable("id") Long id) {
        FundResponseDto dto = fundService.getFund(id);
        return ResponseEntity.ok(dto);
    }

    // 전체 조회 (페이징, 정렬)
    @GetMapping
    public ResponseEntity<Page<FundResponseDto>> listFunds(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "fundTime,desc") String sort) {
        String[] sortParts = sort.split(",");
        Sort s;
        if (sortParts.length == 2) {
            s = Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]);
        } else {
            s = Sort.by(sort);
        }

        Pageable pageable = PageRequest.of(page, size, s);
        Page<FundResponseDto> results = fundService.getFunds(pageable);
        return ResponseEntity.ok(results);
    }

    // 스폰서로 검색 (페이징)
    @GetMapping("/search")
    public ResponseEntity<Page<FundResponseDto>> searchBySponsor(
            @RequestParam("sponsor") String sponsor,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fundTime").descending());
        Page<FundResponseDto> results = fundService.searchBySponsor(sponsor, pageable);
        return ResponseEntity.ok(results);
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<FundResponseDto> updateFund(@PathVariable("id") Long id,
            @Valid @RequestBody FundRequestDto dto) {
        FundResponseDto updated = fundService.updateFund(id, dto);
        return ResponseEntity.ok(updated);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFund(@PathVariable("id") Long id) {
        fundService.deleteFund(id);
        return ResponseEntity.noContent().build();
    }
}