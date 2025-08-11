package com.project.animal.controller;

import com.project.animal.entity.AnimalEntity;
import com.project.animal.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalController {
    private final AnimalService animalService;

    @GetMapping
    public ResponseEntity<List<AnimalEntity>> listAll() {
        return ResponseEntity.ok(animalService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalEntity> get(@PathVariable Long id) {
        AnimalEntity a = animalService.get(id);
        if (a == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(a);
    }

    @PostMapping
    public ResponseEntity<AnimalEntity> create(@RequestBody AnimalEntity animal) {
        return ResponseEntity.ok(animalService.create(animal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalEntity> update(@PathVariable Long id, @RequestBody AnimalEntity animal) {
        if (animal.getAnimalId() == null) animal.setAnimalId(id);
        AnimalEntity exist = animalService.get(id);
        if (exist == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(animalService.update(animal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        animalService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 파일 id 목록 조회
    @GetMapping("/{id}/files")
    public ResponseEntity<Set<Long>> getFileIds(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.findFileIds(id));
    }
}