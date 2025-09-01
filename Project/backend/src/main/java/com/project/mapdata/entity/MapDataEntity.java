package com.project.mapdata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mapdata")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapdata_num")
    private Long mapdataNum; // 지도번호

    @Column(name = "place_name")
    private String placeName;//장소 이름

    @Column(name = "address")
    private String address;//주소

    @Column(name = "latitude")
    private Double latitude;// 위도

    @Column(name = "longitude")
    private Double longitude;// 경도

    @Column(name = "explaination", columnDefinition = "TEXT")
    private String explaination;// 설명
}