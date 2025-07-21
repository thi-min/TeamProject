package com.project.banner.dto;

import com.project.banner.BannerEntity;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannerListDto {
    private Long bannerId;
    private String title;
    private String adminName;
    private Boolean visible;

    public static BannerListDto fromEntity(BannerEntity entity) {
        return new BannerListDto(
            entity.getBannerId(),
            entity.getTitle(),
            entity.getAdmin().getAdminName(),
            entity.getVisible()
        );
    }
}
