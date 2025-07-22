package com.project.banner;

import com.project.banner.dto.*;
import com.project.banner.BannerEntity;
import com.project.banner.BannerRepository;
import com.project.admin.AdminEntity;
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
public class BannerService {

    private final BannerRepository bannerRepository;
    private final String UPLOAD_PATH = "C:/banner-uploads";

    public void createBanner(BannerCreateDto dto, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("이미지 파일은 필수입니다.");
        }

        String fileName = saveFile(file);

        AdminEntity admin = bannerRepository.findAdminByAdminId(dto.getAdminId());
        if (admin == null) throw new RuntimeException("관리자 정보를 찾을 수 없습니다.");

        BannerEntity banner = BannerEntity.builder()
                .title(dto.getTitle())
                .subTitle(dto.getSubTitle())
                .altText(dto.getAltText())
                .linkUrl(dto.getLinkUrl())
                .imageUrl(fileName)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .visible(dto.getVisible())
                .createdAt(LocalDateTime.now())
                .admin(admin)
                .build();

        bannerRepository.save(banner);
    }

    public List<BannerListDto> getAll() {
        return bannerRepository.findAll().stream()
                .map(BannerListDto::fromEntity)
                .collect(Collectors.toList());
    }

    public BannerListDto getDetail(Long id) {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("배너 없음"));
        return BannerListDto.fromEntity(banner);
    }

    public void update(Long id, BannerUpdateDto dto, MultipartFile file) throws IOException {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("배너 없음"));

        banner.setTitle(dto.getTitle());
        banner.setSubTitle(dto.getSubTitle());
        banner.setAltText(dto.getAltText());
        banner.setLinkUrl(dto.getLinkUrl());
        banner.setStartDate(dto.getStartDate());
        banner.setEndDate(dto.getEndDate());
        banner.setVisible(dto.getVisible());
        banner.setUpdatedAt(LocalDateTime.now());

        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            banner.setImageUrl(fileName);
        }

        bannerRepository.save(banner);
    }

    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }

    public void deleteBulk(List<Long> ids) {
        bannerRepository.deleteByBannerIdIn(ids);
    }

    private String saveFile(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_PATH, fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes(), StandardOpenOption.CREATE);
        return fileName;
    }
}
