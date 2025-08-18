package com.project.adopt.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.project.adopt.dto.AdoptRequestDto;
import com.project.adopt.dto.AdoptResponseDto;
import com.project.adopt.entity.AdoptEntity;
import com.project.adopt.service.AdoptService;
import com.project.animal.entity.AnimalEntity;
import com.project.member.entity.MemberEntity;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/adopts")
@RequiredArgsConstructor
public class AdoptController {
    private final AdoptService adoptService;

    private AdoptEntity toEntity(AdoptRequestDto dto) {
        if (dto == null) {
            return null;
        }

        AdoptEntity entity = new AdoptEntity();
        entity.setAdoptNum(dto.getAdoptNum());
        entity.setVistDt(dto.getVistDt());
        entity.setConsultDt(dto.getConsultDt());
        entity.setAdoptTitle(dto.getAdoptTitle());
        entity.setAdoptContent(dto.getAdoptContent());
        entity.setAdoptState(dto.getAdoptState());

        // Member와 Animal 엔티티 설정
        if (dto.getMemberNum() != null) {
            MemberEntity member = new MemberEntity();
            member.setMemberNum(dto.getMemberNum());
            entity.setMember(member);
        }
        if (dto.getAnimalId() != null) {
            AnimalEntity animal = new AnimalEntity();
            animal.setAnimalId(dto.getAnimalId());
            entity.setAnimal(animal);
        }
        return entity;
    }

    private AdoptResponseDto toDto(AdoptEntity entity) {
        if (entity == null) {
            return null;
        }

        AdoptResponseDto dto = new AdoptResponseDto();
        dto.setAdoptNum(entity.getAdoptNum());
        dto.setVistDt(entity.getVistDt());
        dto.setConsultDt(entity.getConsultDt());
        dto.setAdoptTitle(entity.getAdoptTitle());
        dto.setAdoptContent(entity.getAdoptContent());
        dto.setAdoptState(entity.getAdoptState());
        
        // Member와 Animal 정보 설정
        if (entity.getMember() != null) {
            dto.setMemberNum(entity.getMember().getMemberNum());
        }
        if (entity.getAnimal() != null) {
            dto.setAnimalId(entity.getAnimal().getAnimalId());
            dto.setAnimalName(entity.getAnimal().getAnimalName());
        }
        return dto;
    }

    @GetMapping
    public ResponseEntity<List<AdoptResponseDto>> listAll() {
        List<AdoptEntity> list = adoptService.listAll();
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdoptResponseDto> get(@PathVariable Long id) {
        AdoptEntity e = adoptService.get(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(e));
    }

    @PostMapping
    public ResponseEntity<AdoptResponseDto> create(@RequestBody AdoptRequestDto req) {
        AdoptEntity entity = toEntity(req);
        AdoptEntity saved = adoptService.create(entity);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdoptResponseDto> update(@PathVariable Long id, @RequestBody AdoptRequestDto req) {
        AdoptEntity exist = adoptService.get(id);
        if (exist == null) return ResponseEntity.notFound().build();
        AdoptEntity entity = toEntity(req);
        entity.setAdoptNum(id);
        AdoptEntity updated = adoptService.update(entity);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adoptService.delete(id);
        return ResponseEntity.noContent().build();
    }
}