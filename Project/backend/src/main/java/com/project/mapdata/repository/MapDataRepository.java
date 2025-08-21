package com.project.mapdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mapdata.entity.MapDataEntity;

@Repository
public interface MapDataRepository extends JpaRepository<MapDataEntity, Long> {
	
    List<MapDataEntity> findByPlaceNameContainingIgnoreCase(String placeName);
    //장소 이름으로 조회(문자열 포함, 대소문자 구분하지 않고 진행)
}