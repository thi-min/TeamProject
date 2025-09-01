package com.project.banner.controller;

import com.project.banner.dto.BannerRequestDto;
import com.project.banner.dto.BannerResponseDto;
import com.project.banner.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    // 배너 생성
    @PostMapping
    public void createBanner(
        @RequestPart("data") BannerRequestDto dto,
        @RequestPart("file") MultipartFile file
    ) throws IOException {
        bannerService.createBanner(dto, file);
    }

    // 배너 전체 목록 조회
    @GetMapping
    public List<BannerResponseDto> getAll() {
        return bannerService.getAll();
    }

    // 특정 배너 상세 정보 조회
    @GetMapping("/{id}")
    public BannerResponseDto getDetail(@PathVariable Long id) {
        return bannerService.getDetail(id);
    }

    // 배너 수정
    @PutMapping("/{id}")
    public void updateBanner(
        @PathVariable Long id,
        @RequestPart("data") BannerRequestDto dto,
        @RequestPart(name = "file", required = false) MultipartFile file
    ) throws IOException {
        bannerService.update(id, dto, file);
    }

    // 배너 단건 삭제
    @DeleteMapping("/{id}")
    public void deleteBanner(@PathVariable Long id) {
        bannerService.delete(id);
    }
    // 복수 삭제
    @PostMapping("/delete-bulk")
    public void deleteBulk(@RequestBody List<Long> bannerIds) {
    	if (bannerIds == null || bannerIds.isEmpty()) return;
        bannerService.deleteBulk(bannerIds);
    }
    
    
    //25.09.01 안형주 추가
    //활성상태 베너 조회
    @GetMapping("/active")
    public List<BannerResponseDto> getActiveBanners() {
        return bannerService.getActiveBanners();
    }
}
