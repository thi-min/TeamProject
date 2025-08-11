package com.project.adopt.controller;

import com.project.adopt.entity.AdoptEntity;
import com.project.adopt.service.AdoptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adopts")
@RequiredArgsConstructor
public class AdoptController {
    private final AdoptService adoptService;

    @GetMapping
    public ResponseEntity<List<AdoptEntity>> listAll() {
        return ResponseEntity.ok(adoptService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdoptEntity> get(@PathVariable Long id) {
        AdoptEntity e = adoptService.get(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(e);
    }

    @PostMapping
    public ResponseEntity<AdoptEntity> create(@RequestBody AdoptEntity adopt) {
        AdoptEntity saved = adoptService.create(adopt);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdoptEntity> update(@PathVariable Long id, @RequestBody AdoptEntity adopt) {
        if (adopt.getAdoptNum() == null) adopt.setAdoptNum(id);
        AdoptEntity exist = adoptService.get(id);
        if (exist == null) return ResponseEntity.notFound().build();
        AdoptEntity updated = adoptService.update(adopt);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adoptService.delete(id);
        return ResponseEntity.noContent().build();
    }
}