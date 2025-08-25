package com.project.adopt.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.project.adopt.dto.AdoptRequestDto;
import com.project.adopt.dto.AdoptResponseDto;
import com.project.adopt.entity.AdoptEntity;
import com.project.adopt.service.AdoptService;
import com.project.animal.entity.AnimalEntity;
import com.project.common.jwt.JwtTokenProvider;
import com.project.member.dto.MemberMeResponseDto;
import com.project.member.entity.MemberEntity;
import com.project.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/adopt")
@RequiredArgsConstructor
public class AdoptController {
    private final AdoptService adoptService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    
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
    // 신청서 목록 조회 (admin, client)
    @GetMapping("/list")
    public ResponseEntity<Page<AdoptResponseDto>> listAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "adoptNum,desc") String sort) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = jwtTokenProvider.getRoleFromToken(authentication.getCredentials().toString());
        
        // 페이지네이션 및 정렬 설정
        String[] sortParts = sort.split(",");
        Sort s = Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]);
        Pageable pageable = PageRequest.of(page, size, s);
        
        Page<AdoptEntity> adoptPage;

        if ("ADMIN".equals(role)) {
            adoptPage = adoptService.listAll(pageable);
        } else if ("USER".equals(role)) {
            String memberId = authentication.getName();
            MemberMeResponseDto member = memberService.getMyInfo(memberId);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            adoptPage = adoptService.listByMemberNum(member.getMemberNum(), pageable);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Page<AdoptResponseDto> responsePage = adoptPage.map(this::toDto);
        return ResponseEntity.ok(responsePage);
    }
    //신청서 상세 조회(admin, client)
    @GetMapping("/detail/{id}")
    public ResponseEntity<AdoptResponseDto> get(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = jwtTokenProvider.getRoleFromToken(authentication.getCredentials().toString());
        AdoptEntity entity = adoptService.get(id);

        if (entity == null) {
            return ResponseEntity.notFound().build();
        }

        if ("ADMIN".equals(role)) {
            return ResponseEntity.ok(toDto(entity));
        } else if ("USER".equals(role)) {
            String memberId = authentication.getName();
            if (entity.getMember().getMemberId().equals(memberId)) {
                return ResponseEntity.ok(toDto(entity));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    // 입양 신청서 생성(admin)
    @PostMapping("/regist")
    public ResponseEntity<AdoptResponseDto> create(@RequestBody AdoptRequestDto req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = jwtTokenProvider.getRoleFromToken(authentication.getCredentials().toString());
        
        // 클라이언트의 경우, memberNum을 직접 넣지 않고 토큰에서 가져옴
        if ("USER".equals(role)) {
            String memberId = authentication.getName();
            // memberId를 기반으로 memberNum을 찾아 AdoptRequestDto에 설정
            MemberMeResponseDto member = memberService.getMyInfo(memberId);
            req.setMemberNum(member.getMemberNum());
        }

        AdoptEntity entity = toEntity(req);
        AdoptEntity saved = adoptService.create(entity);
        return ResponseEntity.ok(toDto(saved));
    }
    // 입양 신청서 수정(admin)
     @PutMapping("/detail/{id}")
    public ResponseEntity<AdoptResponseDto> update(@PathVariable Long id, @RequestBody AdoptRequestDto req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = jwtTokenProvider.getRoleFromToken(authentication.getCredentials().toString());
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        AdoptEntity exist = adoptService.get(id);
        if (exist == null) return ResponseEntity.notFound().build();
        AdoptEntity entity = toEntity(req);
        entity.setAdoptNum(id);
        AdoptEntity updated = adoptService.update(entity);
        return ResponseEntity.ok(toDto(updated));
    }
    // 입양 신청서 제거(admin)
   @DeleteMapping("/detail/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = jwtTokenProvider.getRoleFromToken(authentication.getCredentials().toString());
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        adoptService.delete(id);
        return ResponseEntity.noContent().build();
    }
}