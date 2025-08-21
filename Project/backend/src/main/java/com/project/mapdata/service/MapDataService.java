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
    
    //지도 정보 전체 조회
    @Transactional(readOnly = true)
    public List<MapDataEntity> findAll() {
        return mapDataRepository.findAll();
    }
    
    //장소 이름으로 조회
    @Transactional(readOnly = true)
    public List<MapDataEntity> findByPlace(String place) {
        return mapDataRepository.findByPlaceNameContainingIgnoreCase(place);
    }
    //지도번호로 조회
    @Transactional(readOnly = true)
    public MapDataEntity get(Long id) {
        return mapDataRepository.findById(id).orElse(null);
    }
    //지도 저장
    @Transactional
    public MapDataEntity create(MapDataEntity e) {
        return mapDataRepository.save(e);
    }
    //지도 갱신
    @Transactional
    public MapDataEntity update(MapDataEntity e) {
        return mapDataRepository.save(e);
    }
    // 지도 제거
    @Transactional
    public void delete(Long id) {
        mapDataRepository.deleteById(id);
    }
}