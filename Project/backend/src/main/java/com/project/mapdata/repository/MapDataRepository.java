package com.project.mapdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mapdata.entity.MapDataEntity;

@Repository
public interface MapDataRepository extends JpaRepository<MapDataEntity, Long> {
    List<MapDataEntity> findByPlaceNameContainingIgnoreCase(String placeName);
}