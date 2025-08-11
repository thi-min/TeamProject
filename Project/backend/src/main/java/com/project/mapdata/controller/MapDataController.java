package com.project.mapdata.controller;

import com.project.mapdata.entity.MapDataEntity;
import com.project.mapdata.service.MapDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mapdata")
@RequiredArgsConstructor
public class MapDataController {
    private final MapDataService mapDataService;

    @GetMapping
    public ResponseEntity<List<MapDataEntity>> listAll() {
        return ResponseEntity.ok(mapDataService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<MapDataEntity>> searchByPlace(@RequestParam("place") String place) {
        return ResponseEntity.ok(mapDataService.findByPlace(place));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MapDataEntity> get(@PathVariable Long id) {
        MapDataEntity e = mapDataService.get(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(e);
    }

    @PostMapping
    public ResponseEntity<MapDataEntity> create(@RequestBody MapDataEntity payload) {
        return ResponseEntity.ok(mapDataService.create(payload));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MapDataEntity> update(@PathVariable Long id, @RequestBody MapDataEntity payload) {
        if (payload.getMapdataNum() == null) payload.setMapdataNum(id);
        return ResponseEntity.ok(mapDataService.update(payload));
    }
}