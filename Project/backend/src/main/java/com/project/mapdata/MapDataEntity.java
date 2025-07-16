package com.project.mapdata;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "map_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MapDataEntity {

	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mapdata_num", length = 1, nullable=false)
	private Integer mapdataNum; // 지도 번호
	
	@Column(name = "place_name", length = 1, nullable=false)
	private String placeName; // 장소 이름
	
	@Column(nullable=false)
	private String address; // 주소
	
	@Column(nullable=false)
	private Double latitude;  // 위도
	
	@Column(nullable=false)
	private Double longitude;  // 경도
	
	@Column(columnDefinition = "TEXT",nullable=false)  //텍스트 표시
	private String explaination; // 설명
	
//	private Integer current_loc;  //현위치 , 카카오 api 지정 후 변경 확인 사항
	
}

//CREATE TABLE map_data (
//mapdata_num INT AUTO_INCREMENT PRIMARY KEY,
//place_name VARCHAR(255),
//address VARCHAR(255),
//latitude DOUBLE,
//longitude DOUBLE,
//explaination TEXT,
//
//);