package com.project.mapdata.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
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
import com.project.mapdata.mapper.MapDataMapper;
import com.project.mapdata.service.MapDataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mapdata")
@RequiredArgsConstructor
public class MapDataController {
    private final MapDataService mapDataService;
    private final MapDataMapper mapDataMapper;

    @GetMapping
    public ResponseEntity<List<MapDataResponseDto>> listAll() {
        return ResponseEntity.ok(mapDataService.findAll().stream().map(mapDataMapper::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MapDataResponseDto>> searchByPlace(@RequestParam("place") String place) {
        return ResponseEntity.ok(mapDataService.findByPlace(place).stream().map(mapDataMapper::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MapDataResponseDto> get(@PathVariable Long id) {
        MapDataEntity e = mapDataService.get(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapDataMapper.toDto(e));
    }

    @PostMapping
    public ResponseEntity<MapDataResponseDto> create(@RequestBody MapDataRequestDto req) {
        MapDataEntity entity = mapDataMapper.toEntity(req);
        return ResponseEntity.ok(mapDataMapper.toDto(mapDataService.create(entity)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MapDataResponseDto> update(@PathVariable Long id, @RequestBody MapDataRequestDto req) {
        MapDataEntity exist = mapDataService.get(id);
        if (exist == null) return ResponseEntity.notFound().build();
        MapDataEntity entity = mapDataMapper.toEntity(req);
        entity.setMapdataNum(id);
        return ResponseEntity.ok(mapDataMapper.toDto(mapDataService.update(entity)));
    }
}