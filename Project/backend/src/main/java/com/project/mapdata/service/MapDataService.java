package com.project.mapdata.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.mapdata.entity.MapDataEntity;
import com.project.mapdata.repository.MapDataRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MapDataService {
    private final MapDataRepository mapDataRepository;

    @Transactional(readOnly = true)
    public List<MapDataEntity> findAll() {
        return mapDataRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<MapDataEntity> findByPlace(String place) {
        return mapDataRepository.findByPlaceNameContainingIgnoreCase(place);
    }

    @Transactional(readOnly = true)
    public MapDataEntity get(Long id) {
        return mapDataRepository.findById(id).orElse(null);
    }

    @Transactional
    public MapDataEntity create(MapDataEntity e) {
        return mapDataRepository.save(e);
    }

    @Transactional
    public MapDataEntity update(MapDataEntity e) {
        return mapDataRepository.save(e);
    }
}