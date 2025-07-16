package com.project.mapdata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapDataDto {

	private String placeName;
	
	private String address;
	
	private Double latitude;
	
	private Double longitude;
	
	
}
