package com.project.mapdata.repository;

import com.project.mapdata.entity.MapDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MapDataRepository extends JpaRepository<MapDataEntity, Long> {
    List<MapDataEntity> findByPlaceNameContainingIgnoreCase(String placeName);
}