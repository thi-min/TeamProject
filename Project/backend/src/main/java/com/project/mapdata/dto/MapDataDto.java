package com.project.adopt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MapDataDto {
    private Long mapdataNum;
    private String placeName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String explaination;
}