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

    @PostMapping
    public void createBanner(@RequestPart("data") BannerCreateDto dto,
                             @RequestPart("file") MultipartFile file) throws IOException {
        bannerService.createBanner(dto, file);
    }

    @GetMapping
    public List<BannerListDto> getAll() {
        return bannerService.getAll();
    }

    @GetMapping("/{id}")
    public BannerListDto getDetail(@PathVariable Long id) {
        return bannerService.getDetail(id);
    }

    @PutMapping("/{id}")
    public void updateBanner(@PathVariable Long id,
                             @RequestPart("data") BannerUpdateDto dto,
                             @RequestPart(name = "file", required = false) MultipartFile file) throws IOException {
        bannerService.update(id, dto, file);
    }

    @DeleteMapping("/{id}")
    public void deleteBanner(@PathVariable Long id) {
        bannerService.delete(id);
    }

    @PostMapping("/delete-bulk")
    public void deleteBulk(@RequestBody BannerDeleteDto dto) {
        bannerService.deleteBulk(dto.getBannerIds());
    }
}
