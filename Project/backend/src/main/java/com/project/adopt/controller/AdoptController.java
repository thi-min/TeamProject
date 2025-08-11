package com.project.adopt.controller;

import com.project.adopt.dto.AdoptRequestDto;
import com.project.adopt.dto.AdoptResponseDto;
import com.project.adopt.entity.AdoptEntity;
import com.project.adopt.mapper.AdoptMapper;
import com.project.adopt.service.AdoptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/adopts")
@RequiredArgsConstructor
public class AdoptController {
    private final AdoptService adoptService;
    private final AdoptMapper adoptMapper;

    @GetMapping
    public ResponseEntity<List<AdoptResponseDto>> listAll() {
        List<AdoptEntity> list = adoptService.listAll();
        return ResponseEntity.ok(list.stream().map(adoptMapper::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdoptResponseDto> get(@PathVariable Long id) {
        AdoptEntity e = adoptService.get(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(adoptMapper.toDto(e));
    }

    @PostMapping
    public ResponseEntity<AdoptResponseDto> create(@RequestBody AdoptRequestDto req) {
        AdoptEntity entity = adoptMapper.toEntity(req);
        AdoptEntity saved = adoptService.create(entity);
        return ResponseEntity.ok(adoptMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdoptResponseDto> update(@PathVariable Long id, @RequestBody AdoptRequestDto req) {
        AdoptEntity exist = adoptService.get(id);
        if (exist == null) return ResponseEntity.notFound().build();
        AdoptEntity entity = adoptMapper.toEntity(req);
        entity.setAdoptNum(id);
        AdoptEntity updated = adoptService.update(entity);
        return ResponseEntity.ok(adoptMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adoptService.delete(id);
        return ResponseEntity.noContent().build();
    }
}