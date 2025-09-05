package com.project.banner.service;

import com.project.admin.entity.AdminEntity;
import com.project.banner.dto.BannerRequestDto;
import com.project.banner.dto.BannerResponseDto;
import com.project.banner.entity.BannerEntity;
import com.project.banner.repository.BannerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path uploadPath;

    // 애플리케이션 시작 시점에 경로 초기화
    @PostConstruct
    public void initPath() {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    // 배너 생성
    @Override
    public void createBanner(BannerRequestDto dto, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("이미지 파일은 필수입니다.");
        }
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new RuntimeException("배너 제목은 필수입니다.");
        }
        if (dto.getVisible() == null) {
            throw new RuntimeException("노출 여부는 필수입니다.");
        }

        String fileName = saveFile(file);

        AdminEntity admin = AdminEntity.builder().adminNum(5L).build();
        
        LocalDate startDate = (dto.getStartDate() != null)
                ? dto.getStartDate()
                : LocalDate.now();

        LocalDate endDate = (dto.getEndDate() != null)
                ? dto.getEndDate()
                : null;
        
        BannerEntity banner = BannerEntity.builder()
                .title(dto.getTitle())
                .subTitle(dto.getSubTitle())
                .altText(dto.getAltText())
                .linkUrl(dto.getLinkUrl())
                .imageUrl(fileName)
                .startDate(startDate)
                .endDate(endDate)
                .visible(dto.getVisible())
                .createdAt(LocalDateTime.now())
                .admin(admin)
                .build();

        bannerRepository.save(banner);
    }

    // 전체 조회
    @Override
    public List<BannerResponseDto> getAll() {
        return bannerRepository.findAll().stream()
                .map(BannerResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 상세 조회
    @Override
    public BannerResponseDto getDetail(Long id) {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("배너 없음"));
        
        LocalDate today = LocalDate.now();
        if (banner.getEndDate() != null && banner.getEndDate().isBefore(today)) {
            banner.setVisible(false); // 조회 시점에서만 변경 (DB 반영X)
        }

        return BannerResponseDto.fromEntity(banner);
    }

    // 수정
    @Override
    public void update(Long id, BannerRequestDto dto, MultipartFile file) throws IOException {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("배너 없음"));

        banner.setTitle(dto.getTitle());
        banner.setSubTitle(dto.getSubTitle());
        banner.setAltText(dto.getAltText());
        banner.setLinkUrl(dto.getLinkUrl());
        banner.setStartDate(dto.getStartDate() != null ? dto.getStartDate() : LocalDate.now());
        banner.setEndDate(dto.getEndDate());
        banner.setVisible(dto.getVisible());
        banner.setUpdatedAt(LocalDateTime.now());

        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            banner.setImageUrl(fileName);
        }

        bannerRepository.save(banner);
    }

    // 단일 삭제
    @Override
    public void delete(Long id) {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("배너 없음"));
        try {
            deleteFile(banner.getImageUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        bannerRepository.deleteById(id);
    }

    // 복수 삭제
    @Override
    public void deleteBulk(List<Long> ids) {
        List<BannerEntity> banners = bannerRepository.findAllById(ids);
        for (BannerEntity banner : banners) {
            try {
                deleteFile(banner.getImageUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bannerRepository.deleteByBannerIdIn(ids);
    }

    // 파일 삭제
    private void deleteFile(String fileName) throws IOException {
        Path path = uploadPath.resolve(fileName);
        Files.deleteIfExists(path);
    }

    // 파일 저장
    private String saveFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new RuntimeException("파일 이름이 유효하지 않습니다.");
        }

        long maxFileSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("파일 크기는 5MB를 초과할 수 없습니다.");
        }

        String lowerFileName = originalFileName.toLowerCase();
        if (!(lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg") || lowerFileName.endsWith(".png"))) {
            throw new RuntimeException("jpg, jpeg, png 형식의 파일만 업로드 가능합니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
            throw new RuntimeException("jpg/jpeg/png 형식의 이미지 파일만 허용됩니다.");
        }

        String fileName = System.currentTimeMillis() + "_" + originalFileName;
        Path path = uploadPath.resolve(fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes(), StandardOpenOption.CREATE);
        return fileName;
    }
    
    //25.09.01 안형주 추가
    //활성상태 베너 조회
    @Override
    public List<BannerResponseDto> getActiveBanners() {
    	LocalDate today = LocalDate.now();
    	return bannerRepository.findActiveBanners(today).stream()
                .map(BannerResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
