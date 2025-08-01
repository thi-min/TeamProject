package com.project.banner.dto;

import com.project.banner.BannerEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerListDto {

    private Long bannerId;            // 배너 고유 ID
    private String title;             // 제목
    private String subTitle;          // 부제목
    private String imageUrl;          // 이미지 경로
    private String altText;           // 이미지 대체 텍스트
    private String linkUrl;           // 배너 클릭 시 이동할 링크
    private LocalDate startDate;      // 노출 시작일
    private LocalDate endDate;        // 노출 종료일
    private Boolean visible;          // 노출 여부
    private LocalDateTime createdAt;  // 생성일
    private String adminName;         // 등록한 관리자 이름

    public static BannerListDto fromEntity(BannerEntity entity) {
        return BannerListDto.builder()
            .bannerId(entity.getBannerId())
            .title(entity.getTitle())
            .subTitle(entity.getSubTitle())
            .imageUrl(entity.getImageUrl())
            .altText(entity.getAltText())
            .linkUrl(entity.getLinkUrl())
            .startDate(entity.getStartDate())
            .endDate(entity.getEndDate())
            .visible(entity.getVisible())
            .createdAt(entity.getCreatedAt())
            .adminName(entity.getAdmin() != null ? entity.getAdmin().getAdminName() : null)
            .build();
    }
}
