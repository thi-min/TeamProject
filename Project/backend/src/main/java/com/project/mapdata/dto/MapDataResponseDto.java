package com.project.mapdata.dto;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapDataResponseDto {
    private Long mapdataNum;
    private String placeName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String explaination;
}