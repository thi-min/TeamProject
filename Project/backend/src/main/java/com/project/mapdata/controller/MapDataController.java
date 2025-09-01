package com.project.mapdata.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.mapdata.dto.MapDataRequestDto;
import com.project.mapdata.dto.MapDataResponseDto;
import com.project.mapdata.entity.MapDataEntity;
import com.project.mapdata.service.MapDataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mapdata")
@RequiredArgsConstructor
public class MapDataController {
    private final MapDataService mapDataService;
    // 지도 데이터 목록 조회
    @GetMapping
    public ResponseEntity<List<MapDataResponseDto>> listAll() {
        return ResponseEntity.ok(mapDataService.findAll().stream().map(this::toDto).collect(Collectors.toList()));
    }
    // 장소명으로 검색
    @GetMapping("/search")
    public ResponseEntity<List<MapDataResponseDto>> searchByPlace(@RequestParam("place") String place) {
        return ResponseEntity.ok(mapDataService.findByPlace(place).stream().map(this::toDto).collect(Collectors.toList()));
    }
    // 단건 데이터 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<MapDataResponseDto> get(@PathVariable Long id) {
        MapDataEntity e = mapDataService.get(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(this.toDto(e));
    }
    // 데이터 생성
    @PostMapping
    public ResponseEntity<MapDataResponseDto> create(@RequestBody MapDataRequestDto req) {
        MapDataEntity entity = this.toEntity(req);
        return ResponseEntity.ok(this.toDto(mapDataService.create(entity)));
    }
    // 데이터 수정
    @PutMapping("/{id}")
    public ResponseEntity<MapDataResponseDto> update(@PathVariable Long id, @RequestBody MapDataRequestDto req) {
        MapDataEntity exist = mapDataService.get(id);
        if (exist == null) return ResponseEntity.notFound().build();
        MapDataEntity entity = this.toEntity(req);
        entity.setMapdataNum(id);
        return ResponseEntity.ok(this.toDto(mapDataService.update(entity)));
    }
    // 데이터 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mapDataService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // DTO -> Entity 변환 메서드
    private MapDataEntity toEntity(MapDataRequestDto dto) {
        if (dto == null) {
            return null;
        }
        MapDataEntity entity = new MapDataEntity();
        // 필드명 불일치 수정
        entity.setMapdataNum(dto.getMapdataNum());
        entity.setPlaceName(dto.getPlaceName());
        entity.setAddress(dto.getAddress());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setExplaination(dto.getExplaination());
        return entity;
    }

    // Entity -> DTO 변환 메서드
    private MapDataResponseDto toDto(MapDataEntity entity) {
        if (entity == null) {
            return null;
        }
        MapDataResponseDto dto = new MapDataResponseDto();
        dto.setMapdataNum(entity.getMapdataNum());
        dto.setPlaceName(entity.getPlaceName());
        dto.setAddress(entity.getAddress());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setExplaination(entity.getExplaination());
        return dto;
    }
}