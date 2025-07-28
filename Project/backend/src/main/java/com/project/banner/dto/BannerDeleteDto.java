package com.project.banner.dto;

import java.util.List;

import lombok.*;

//베너 삭제
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BannerDeleteDto {

	private List<Long> bannerIds;
}