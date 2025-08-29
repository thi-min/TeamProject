package com.project.banner.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @JsonProperty("visible")
    private Boolean visible;
}
