package com.project.animal.controller;


import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.animal.dto.AnimalRequestDto;
import com.project.animal.dto.AnimalResponseDto;
import com.project.animal.entity.AnimalEntity;
import com.project.animal.entity.AnimalFileEntity;
import com.project.animal.service.AnimalService;
import com.project.common.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;
    private final JwtTokenProvider jwtTokenProvider;

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

        Set<Long> fileIds = entity.getFiles() != null ?
                entity.getFiles().stream()
                        .map(AnimalFileEntity::getAnimalFileId)
                        .collect(Collectors.toSet()) :
                Collections.emptySet();
        dto.setFileIds(fileIds);

        return dto;
    }

    /**
     * 모든 동물 목록 조회 (관리자/클라이언트)
     * - 클라이언트는 권한 없이 접근 가능
     */
     @GetMapping("/list")
    public ResponseEntity<Page<AnimalResponseDto>> listAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "animalDate,desc") String sort) {
        
        String[] sortParts = sort.split(",");
        Sort s = Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]);
        Pageable pageable = PageRequest.of(page, size, s);
        
        Page<AnimalEntity> animalPage = animalService.listAll(pageable);
        
        Page<AnimalResponseDto> responsePage = animalPage.map(this::toDto);
        return ResponseEntity.ok(responsePage);
    }

    /**
     * 특정 동물 정보 조회 (관리자/클라이언트)
     * - 클라이언트는 권한 없이 접근 가능
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<AnimalResponseDto> get(@PathVariable Long id) {
        AnimalEntity e = animalService.get(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(e));
    }

    /**
     * 동물 정보 생성 (관리자 전용)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/regist")
    public ResponseEntity<AnimalResponseDto> create(@RequestBody AnimalRequestDto req) {
        // 별도의 권한 체크 로직이 필요 없음
        AnimalEntity entity = toEntity(req);
        AnimalEntity saved = animalService.create(entity);
        return ResponseEntity.ok(toDto(saved));
    }

    /**
     * 동물 정보 수정 (관리자 전용)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/detail/{id}")
    public ResponseEntity<AnimalResponseDto> update(@PathVariable Long id, @RequestBody AnimalRequestDto req) {
        AnimalEntity exist = animalService.get(id);
        if (exist == null) return ResponseEntity.notFound().build();

        // 필요한 필드만 갱신
        exist.setAnimalName(req.getAnimalName());
        exist.setAnimalBreed(req.getAnimalBreed());
        exist.setAnimalSex(req.getAnimalSex());
        exist.setAnimalState(req.getAnimalState());
        exist.setAnimalDate(req.getAnimalDate());
        exist.setAdoptDate(req.getAdoptDate());
        exist.setAnimalContent(req.getAnimalContent());

        AnimalEntity updated = animalService.update(exist);
        return ResponseEntity.ok(toDto(updated));
    }

    /**
     * 동물 정보 삭제 (관리자 전용)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/detail/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // The @PreAuthorize annotation already ensures this is called by an ADMIN.
        // The following manual check is not necessary.
        /*
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = jwtTokenProvider.getRoleFromToken(authentication.getCredentials().toString());
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        */
        animalService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 파일 id 조회 (관리자 전용)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/files")
    public ResponseEntity<Set<Long>> getFileIds(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = jwtTokenProvider.getRoleFromToken(authentication.getCredentials().toString());
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(animalService.findFileIds(id));
    }
}