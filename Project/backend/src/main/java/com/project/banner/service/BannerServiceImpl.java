package com.project.banner.service;

import com.project.admin.entity.AdminEntity;
import com.project.banner.dto.BannerRequestDto;
import com.project.banner.dto.BannerResponseDto;
import com.project.banner.entity.BannerEntity;
import com.project.banner.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    // 배너 이미지 저장 경로
    private final String UPLOAD_PATH = "src/main/resources/static/banner-uploads"; 

    @Override
    public void createBanner(BannerRequestDto dto, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("이미지 파일은 필수입니다.");
        }

        // 이미지 파일 저장
        String fileName = saveFile(file);
        
        AdminEntity admin = AdminEntity.builder().adminNum(5L).build();

        // 배너 엔티티 생성 및 저장
        BannerEntity banner = BannerEntity.builder()
                .title(dto.getTitle())
                .subTitle(dto.getSubTitle())
                .altText(dto.getAltText())
                .linkUrl(dto.getLinkUrl())
                .imageUrl(fileName)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isVisible(dto.getIsVisible())
                .createdAt(LocalDateTime.now())
                .admin(admin) 
                .build();

        bannerRepository.save(banner);
    }

    @Override
    public List<BannerResponseDto> getAll() {
        return bannerRepository.findAll().stream()
                .map(BannerResponseDto::fromEntity) // ✅ ResponseDto 변환 메서드 필요
                .collect(Collectors.toList());
    }

    @Override
    public BannerResponseDto getDetail(Long id) {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("배너 없음"));
        return BannerResponseDto.fromEntity(banner); // ✅ ResponseDto 변환
    }

    @Override
    public void update(Long id, BannerRequestDto dto, MultipartFile file) throws IOException {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("배너 없음"));

        // 필드 수정
        banner.setTitle(dto.getTitle());
        banner.setSubTitle(dto.getSubTitle());
        banner.setAltText(dto.getAltText());
        banner.setLinkUrl(dto.getLinkUrl());
        banner.setStartDate(dto.getStartDate());
        banner.setEndDate(dto.getEndDate());
        banner.setIsVisible(dto.getIsVisible());
        banner.setUpdatedAt(LocalDateTime.now());

        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            banner.setImageUrl(fileName);
        }

        bannerRepository.save(banner);
    }

    @Override
    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }

    @Override
    public void deleteBulk(List<Long> ids) {
        bannerRepository.deleteByBannerIdIn(ids);
    }

    // 파일 저장
    private String saveFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new RuntimeException("파일 이름이 유효하지 않습니다.");
        }

        // 1. 파일 크기 제한 (5MB)
        long maxFileSize = 5 * 1024 * 1024;
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("파일 크기는 5MB를 초과할 수 없습니다.");
        }

        // 2. 확장자 검사
        String lowerFileName = originalFileName.toLowerCase();
        if (!(lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg"))) {
            throw new RuntimeException("jpg 또는 jpeg 형식의 파일만 업로드 가능합니다.");
        }

        // 3. MIME 타입 검사
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("image/jpeg")) {
            throw new RuntimeException("jpg/jpeg 형식의 이미지 파일만 허용됩니다.");
        }

        // 4. 파일 저장
        String fileName = System.currentTimeMillis() + "_" + originalFileName;
        Path path = Paths.get(UPLOAD_PATH, fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes(), StandardOpenOption.CREATE);
        return fileName;
    }
}