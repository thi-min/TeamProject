package com.project.service.mapData;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.entity.mapdata.MapDataEntity;
import com.project.repository.mapData.MapDataRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MapDataService {

	private final MapDataRepository mapDataRepository;
	
	public void saveMapData(MapDataEntity entity) {
		mapDataRepository.save(entity);
	}
	
	public List<MapDataEntity> getAllMapData(){
		return mapDataRepository.findAll();
	}
}
