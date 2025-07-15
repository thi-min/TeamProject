package com.project.entity.mapdata;

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
	private Integer mapdata_num; // 지도 번호
	
	private String place_name; // 장소 이름
	
	private String address; // 주소
	
	private Double latitude;  // 위도
	
	private Double longitude;  // 경도
	
	@Column(columnDefinition = "TEXT")  //텍스트 표시
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