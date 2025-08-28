package com.project.banner.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.banner.entity.BannerEntity;

import lombok.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class BannerResponseDto {
    private Long bannerId;	//배너 고유 아이디
    private String title;		//제목
    private String subTitle;	//부제목
    private String imageUrl;	//이미지 경로
    private String altText;		//이미지 대체 텍스트
    private String linkUrl;		//링크 경로
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;	//배너 노출 시작일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;		//배너 노출 종료일
    @JsonProperty("visible")
    private Boolean visible;		//배너 노출 여부
    private LocalDateTime createdAt;	//배너 생성일시
    private LocalDateTime updatedAt;	//배너 수정일시
    
    public static BannerResponseDto fromEntity(BannerEntity banner) {
        return BannerResponseDto.builder()
                .bannerId(banner.getBannerId())
                .title(banner.getTitle())
                .subTitle(banner.getSubTitle())
                .imageUrl(banner.getImageUrl())
                .altText(banner.getAltText())
                .linkUrl(banner.getLinkUrl())
                .startDate(banner.getStartDate())
                .endDate(banner.getEndDate())
                .visible(banner.getVisible())
                .createdAt(banner.getCreatedAt())
                .updatedAt(banner.getUpdatedAt())
                .build();
    }
}