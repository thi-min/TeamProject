package com.project.banner.dto;

import java.time.LocalDate;

import lombok.*;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class BannerRequestDto {
    private Long bannerId;        // 수정 시 필요 (생성할 땐 null)
    private String title;		//제목
    private String subTitle;	//부제목
    private String imageUrl;	//이미지 경로
    private String altText;		//이미지 대체 텍스트
    private String linkUrl;		//링크 경로
    private LocalDate startDate;	//배너 노출 시작일
    private LocalDate endDate;		//배너 노출 종료일
    private Boolean isVisible;	//배너 노출 여부
}
