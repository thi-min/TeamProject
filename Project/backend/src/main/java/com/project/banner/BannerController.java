package com.project.banner;

import com.project.banner.BannerService;
import com.project.banner.dto.BannerCreateDto;
import com.project.banner.dto.BannerDeleteDto;
import com.project.banner.dto.BannerListDto;
import com.project.banner.dto.BannerUpdateDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    // 배너 생성
    @PostMapping
    public void createBanner(@RequestPart("data") BannerCreateDto dto,
                             @RequestPart("file") MultipartFile file) throws IOException {
        bannerService.createBanner(dto, file);
    }

    // 배너 전체 목록 조회
    @GetMapping
    public List<BannerListDto> getAll() {
        return bannerService.getAll();
    }

    // 특정 배너 상세 정보 조회
    @GetMapping("/{id}")
    public BannerListDto getDetail(@PathVariable Long id) {
        return bannerService.getDetail(id);
    }

    // 배너 수정
    @PutMapping("/{id}")
    public void updateBanner(@PathVariable Long id,
                             @RequestPart("data") BannerUpdateDto dto,
                             @RequestPart(name = "file", required = false) MultipartFile file) throws IOException {
        bannerService.update(id, dto, file);
    }

    // 배너 단건 삭제
    @DeleteMapping("/{id}")
    public void deleteBanner(@PathVariable Long id) {
        bannerService.delete(id);
    }

    // 배너 복수건 삭제
    @PostMapping("/delete-bulk")
    public void deleteBulk(@RequestBody BannerDeleteDto dto) {
        bannerService.deleteBulk(dto.getBannerIds());
    }
}
