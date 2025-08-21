package com.project.adopt.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

import jakarta.servlet.http.HttpServletRequest;
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
    //신청서 목록 조회(admin, client)
    @GetMapping
    public ResponseEntity<List<AdoptResponseDto>> listAll(HttpServletRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        if (request.isUserInRole("ADMIN")) {
            // 관리자: 모든 신청서 조회
            List<AdoptEntity> list = adoptService.listAll();
            return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
        } else {
            // 일반 사용자: 자신의 신청서만 조회
            Long memberNum = ((MemberEntity) userDetails).getMemberNum(); // UserDetails에서 memberNum 추출 로직
            List<AdoptEntity> list = adoptService.listByMemberNum(memberNum);
            return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
        }
    }
    //신청서 상세 조회(admin, client)
    @GetMapping("/{id}")
    public ResponseEntity<AdoptResponseDto> get(@PathVariable Long id, HttpServletRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        AdoptEntity e = adoptService.get(id);

        if (e == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.isUserInRole("ADMIN")) {
            // 관리자: 모든 신청서 조회
            return ResponseEntity.ok(toDto(e));
        } else {
            // 일반 사용자: 자신의 신청서만 조회
            Long memberNum = ((MemberEntity) userDetails).getMemberNum(); // UserDetails에서 memberNum 추출 로직
            if (e.getMember().getMemberNum().equals(memberNum)) {
                return ResponseEntity.ok(toDto(e));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 권한 없음
            }
        }
    }
    // 입양 신청서 생성(admin)
    @PostMapping
    public ResponseEntity<AdoptResponseDto> create(@RequestBody AdoptRequestDto req) {
        AdoptEntity entity = toEntity(req);
        AdoptEntity saved = adoptService.create(entity);
        return ResponseEntity.ok(toDto(saved));
    }
    // 입양 신청서 수정(admin)
    @PutMapping("/{id}")
    public ResponseEntity<AdoptResponseDto> update(@PathVariable Long id, @RequestBody AdoptRequestDto req) {
        AdoptEntity exist = adoptService.get(id);
        if (exist == null) return ResponseEntity.notFound().build();
        AdoptEntity entity = toEntity(req);
        entity.setAdoptNum(id);
        AdoptEntity updated = adoptService.update(entity);
        return ResponseEntity.ok(toDto(updated));
    }
    // 입양 신청서 제거(admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adoptService.delete(id);
        return ResponseEntity.noContent().build();
    }
}