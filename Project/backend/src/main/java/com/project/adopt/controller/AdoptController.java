package com.project.adopt.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.project.animal.service.AnimalService;
import com.project.common.jwt.JwtTokenProvider;
import com.project.member.dto.MemberMeResponseDto;
import com.project.member.entity.MemberEntity;
import com.project.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/adopts")
@RequiredArgsConstructor
public class AdoptController {
    private final AdoptService adoptService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AnimalService animalService;

    // ----------------- DTO ↔ Entity 변환 -----------------
    private AdoptEntity toEntity(AdoptRequestDto dto) {
        if (dto == null) return null;

        AdoptEntity entity = new AdoptEntity();
        entity.setAdoptNum(dto.getAdoptNum());
        entity.setVistDt(dto.getVistDt());
        entity.setConsultDt(dto.getConsultDt());
        entity.setAdoptTitle(dto.getAdoptTitle());
        entity.setAdoptContent(dto.getAdoptContent());
        entity.setAdoptState(dto.getAdoptState());

        if (dto.getMemberNum() != null) {
            MemberEntity member = memberService.findByMemberNum(dto.getMemberNum()); 
            entity.setMember(member);
        }

        if (dto.getAnimalId() != null) {
            AnimalEntity animal = new AnimalEntity();
            animal.setAnimalId(dto.getAnimalId());
            entity.setAnimal(animal);
        }
        return entity;
    }
    private AdoptEntity toEntity(AdoptRequestDto dto, Long id) {
        AdoptEntity entity = toEntity(dto); // 기존 메서드 호출
        entity.setAdoptNum(id); // path variable id로 덮어쓰기
        return entity;
    }
//    private AdoptEntity toEntity(AdoptRequestDto dto) {
//        if (dto == null) return null;
//
//        AdoptEntity entity = new AdoptEntity();
//        entity.setAdoptNum(dto.getAdoptNum());
//        entity.setVistDt(dto.getVistDt());
//        entity.setConsultDt(dto.getConsultDt());
//        entity.setAdoptTitle(dto.getAdoptTitle());
//        entity.setAdoptContent(dto.getAdoptContent());
//        entity.setAdoptState(dto.getAdoptState());
//
//        if (dto.getMemberNum() != null) {
//            MemberEntity member = new MemberEntity();
//            member.setMemberNum(dto.getMemberNum());
//            entity.setMember(member);
//        }
//        if (dto.getAnimalId() != null) {
//            AnimalEntity animal = new AnimalEntity();
//            animal.setAnimalId(dto.getAnimalId());
//            entity.setAnimal(animal);
//        }
//        return entity;
//    }

    private AdoptResponseDto toDto(AdoptEntity entity) {
        if (entity == null) return null;

        AdoptResponseDto dto = new AdoptResponseDto();
        dto.setAdoptNum(entity.getAdoptNum());
        dto.setVistDt(entity.getVistDt());
        dto.setConsultDt(entity.getConsultDt());
        dto.setAdoptTitle(entity.getAdoptTitle());
        dto.setAdoptContent(entity.getAdoptContent());
        dto.setAdoptState(entity.getAdoptState());

        if (entity.getMember() != null) {
            dto.setMemberNum(entity.getMember().getMemberNum());
            dto.setMemberName(entity.getMember().getMemberName());
        }
        if (entity.getAnimal() != null) {
            dto.setAnimalId(entity.getAnimal().getAnimalId());
            dto.setAnimalName(entity.getAnimal().getAnimalName());
        }
        return dto;
    }

    // ----------------- JWT에서 Role/Member 추출 -----------------
    private String getRoleFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        String token = header.substring(7);
        return jwtTokenProvider.getRoleFromToken(token);
    }

    private MemberMeResponseDto getMemberFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        String token = header.substring(7);
        String memberId = jwtTokenProvider.getMemberIdFromToken(token);
        return memberService.getMyInfo(memberId);
    }

    // ----------------- 신청서 목록 조회 -----------------
    @GetMapping("/list")
    public ResponseEntity<Page<AdoptResponseDto>> listAll(
            HttpServletRequest request,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "adoptNum,desc") String sort) {

        String role = getRoleFromRequest(request);
        if (role == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        String[] sortParts = sort.split(",");
        Sort s = Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]);
        Pageable pageable = PageRequest.of(page, size, s);

        Page<AdoptEntity> adoptPage;

        if ("ADMIN".equals(role)) {
            adoptPage = adoptService.listAll(pageable);
        } else if ("USER".equals(role)) {
            MemberMeResponseDto member = getMemberFromRequest(request);
            if (member == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            adoptPage = adoptService.listByMemberNum(member.getMemberNum(), pageable);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(adoptPage.map(this::toDto));
    }

    // ----------------- 신청서 상세 조회 -----------------
    @GetMapping("/detail/{id}")
    public ResponseEntity<AdoptResponseDto> get(@PathVariable Long id, HttpServletRequest request) {
        String role = getRoleFromRequest(request);
        if (role == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        AdoptEntity entity = adoptService.get(id);
        if (entity == null) return ResponseEntity.notFound().build();

        if ("ADMIN".equals(role)) {
            return ResponseEntity.ok(toDto(entity));
        } else if ("USER".equals(role)) {
            MemberMeResponseDto member = getMemberFromRequest(request);
            if (member == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            if (!entity.getMember().getMemberNum().equals(member.getMemberNum())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(toDto(entity));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // ----------------- 신청서 생성 -----------------
    @PostMapping("/regist")
    public ResponseEntity<AdoptResponseDto> create(@RequestBody AdoptRequestDto req, HttpServletRequest request) {
        String role = getRoleFromRequest(request);
        if (!"ADMIN".equals(role)) { // USER는 사용하지 않음
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // ADMIN이 작성 → 요청 DTO에서 memberNum이 필수
        if (req.getMemberNum() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        AdoptEntity saved = adoptService.create(toEntity(req));
        return ResponseEntity.ok(toDto(saved));
    }


    // ----------------- 신청서 수정 -----------------
    @PutMapping("/detail/{id}")
    public ResponseEntity<AdoptResponseDto> update(@PathVariable Long id, @RequestBody AdoptRequestDto req, HttpServletRequest request) {
        String role = getRoleFromRequest(request);
        if (!"ADMIN".equals(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        AdoptEntity exist = adoptService.get(id);
        if (exist == null) return ResponseEntity.notFound().build();

        AdoptEntity updated = adoptService.update(toEntity(req, id));
        return ResponseEntity.ok(toDto(updated));
    }

    // ----------------- 신청서 삭제 -----------------
    @DeleteMapping("/detail/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        String role = getRoleFromRequest(request);
        if (!"ADMIN".equals(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        adoptService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
