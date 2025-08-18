package com.project.animal.controller;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.animal.dto.AnimalRequestDto;
import com.project.animal.dto.AnimalResponseDto;
import com.project.animal.entity.AnimalEntity;
import com.project.animal.entity.AnimalFileEntity;
import com.project.animal.service.AnimalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalController {
    private final AnimalService animalService;

    // DTO와 Entity를 수동으로 변환하는 메서드 추가
    private AnimalEntity toEntity(AnimalRequestDto dto) {
        if (dto == null) {
            return null;
        }

        AnimalEntity entity = new AnimalEntity();
        entity.setAnimalId(dto.getAnimalId());
        entity.setAnimalName(dto.getAnimalName());
        entity.setAnimalBreed(dto.getAnimalBreed());
        entity.setAnimalSex(dto.getAnimalSex());
        entity.setAnimalState(dto.getAnimalState());
        entity.setAnimalDate(dto.getAnimalDate());
        entity.setAdoptDate(dto.getAdoptDate());
        entity.setAnimalContent(dto.getAnimalContent());
        return entity;
    }

    private AnimalResponseDto toDto(AnimalEntity entity) {
        if (entity == null) {
            return null;
        }

        AnimalResponseDto dto = new AnimalResponseDto();
        dto.setAnimalId(entity.getAnimalId());
        dto.setAnimalName(entity.getAnimalName());
        dto.setAnimalBreed(entity.getAnimalBreed());
        dto.setAnimalSex(entity.getAnimalSex());
        dto.setAnimalState(entity.getAnimalState());
        dto.setAnimalDate(entity.getAnimalDate());
        dto.setAdoptDate(entity.getAdoptDate());
        dto.setAnimalContent(entity.getAnimalContent());

     // 파일 ID 목록 설정. files 컬렉션이 null이 아니면 스트림 처리, 아니면 빈 Set으로 설정.
        Set<Long> fileIds = entity.getFiles() != null ?
                                  entity.getFiles().stream()
                                  .map(AnimalFileEntity::getAnimalFileId)
                                  .collect(Collectors.toSet())
                                  : Collections.emptySet(); // null 대신 빈 Set을 반환하여 NPE 방지
        dto.setFileIds(fileIds);

        return dto;
    }

    @GetMapping
    public ResponseEntity<List<AnimalResponseDto>> listAll() {
        return ResponseEntity.ok(animalService.listAll().stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponseDto> get(@PathVariable Long id) {
        AnimalEntity e = animalService.get(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(e));
    }

    @PostMapping
    public ResponseEntity<AnimalResponseDto> create(@RequestBody AnimalRequestDto req) {
        AnimalEntity entity = toEntity(req);
        AnimalEntity saved = animalService.create(entity);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponseDto> update(@PathVariable Long id, @RequestBody AnimalRequestDto req) {
        AnimalEntity exist = animalService.get(id);
        if (exist == null) return ResponseEntity.notFound().build();
        AnimalEntity entity = toEntity(req);
        entity.setAnimalId(id);
        return ResponseEntity.ok(toDto(animalService.update(entity)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        animalService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/files")
    public ResponseEntity<Set<Long>> getFileIds(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.findFileIds(id));
    }
}