package com.project.repository.mapData;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.mapdata.MapDataEntity;

public interface MapDataRepository extends JpaRepository<MapDataEntity, Integer> {

}
