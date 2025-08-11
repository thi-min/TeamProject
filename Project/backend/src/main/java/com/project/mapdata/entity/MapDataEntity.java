package com.project.mapdata.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "mapdata")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapdata_num")
    private Long mapdataNum;

    @Column(name = "place_name")
    private String placeName;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "explaination", columnDefinition = "TEXT")
    private String explaination;
}