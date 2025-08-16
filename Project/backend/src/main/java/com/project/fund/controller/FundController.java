package com.project.fund.controller;

import com.project.fund.dto.FundRequestDto;
import com.project.fund.dto.FundResponseDto;
import com.project.fund.service.FundService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 전체 페이징 조회 (관리자용)
    @GetMapping
    public ResponseEntity<Page<FundResponseDto>> getFunds(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "fundTime,desc") String sort) {

        // sort 파싱: "field,dir"
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