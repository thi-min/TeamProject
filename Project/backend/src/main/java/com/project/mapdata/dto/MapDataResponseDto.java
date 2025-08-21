package com.project.mapdata.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapDataResponseDto {
    private Long mapdataNum; // 지도번호
    private String placeName; // 장소 이름
    private String address; // 주소
    private Double latitude; // 위도
    private Double longitude; // 경도
    private String explaination; // 설명
}