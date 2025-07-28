package com.project.banner.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannerCreateDto {
    private String title;
    private String subTitle;
    private String imageUrl;
    private String altText;
    private String linkUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean visible;
    private String adminId;
}