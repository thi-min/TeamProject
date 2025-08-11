package com.project.animal.controller;

import com.project.animal.dto.AnimalRequestDto;
import com.project.animal.dto.AnimalResponseDto;
import com.project.animal.entity.AnimalEntity;
import com.project.animal.mapper.AnimalMapper;
import com.project.animal.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalController {
    private final AnimalService animalService;
    private final AnimalMapper animalMapper;

    @GetMapping
    public ResponseEntity<List<AnimalResponseDto>> listAll() {
        return ResponseEntity.ok(animalService.listAll().stream().map(animalMapper::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponseDto> get(@PathVariable Long id) {
        AnimalEntity e = animalService.get(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(animalMapper.toDto(e));
    }

    @PostMapping
    public ResponseEntity<AnimalResponseDto> create(@RequestBody AnimalRequestDto req) {
        AnimalEntity entity = animalMapper.toEntity(req);
        AnimalEntity saved = animalService.create(entity);
        return ResponseEntity.ok(animalMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponseDto> update(@PathVariable Long id, @RequestBody AnimalRequestDto req) {
        AnimalEntity exist = animalService.get(id);
        if (exist == null) return ResponseEntity.notFound().build();
        AnimalEntity entity = animalMapper.toEntity(req);
        entity.setAnimalId(id);
        return ResponseEntity.ok(animalMapper.toDto(animalService.update(entity)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        animalService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/files")
    public ResponseEntity<java.util.Set<Long>> getFileIds(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.findFileIds(id));
    }
}