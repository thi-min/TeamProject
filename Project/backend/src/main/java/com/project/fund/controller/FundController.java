package com.project.fund.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.project.common.jwt.JwtTokenProvider;
import com.project.fund.dto.FundRequestDto;
import com.project.fund.dto.FundResponseDto;
import com.project.fund.entity.FundEntity;
import com.project.fund.service.FundService;
import com.project.member.dto.MemberMeResponseDto;
import com.project.member.entity.MemberEntity;
import com.project.member.service.MemberService;

import jakarta.validation.Valid;

/**
 * Fund API Controller
 *
 * 기본 경로: /api/funds
 */
@RestController
@RequestMapping("/funds")
public class FundController {

    private final FundService fundService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    public FundController(FundService fundService, MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.fundService = fundService;
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * DTO를 Entity로 변환하는 메서드
     * @param dto FundRequestDto
     * @return FundEntity
     */
    private FundEntity toEntity(FundRequestDto dto) {
        if (dto == null) {
            return null;
        }

        FundEntity.FundEntityBuilder builder = FundEntity.builder()
                .fundSponsor(dto.getFundSponsor())
                .fundPhone(dto.getFundPhone())
                .fundBirth(dto.getFundBirth())
                .fundType(dto.getFundType())
                .fundMoney(dto.getFundMoney())
                .fundTime(dto.getFundTime())
                .fundItem(dto.getFundItem())
                .fundNote(dto.getFundNote())
                .fundBank(dto.getFundBank())
                .fundAccountNum(dto.getFundAccountNum())
                .fundDepositor(dto.getFundDepositor())
                .fundDrawlDate(dto.getFundDrawlDate())
                .fundCheck(dto.getFundCheck());

        // MemberEntity 생성자 오류를 해결하기 위해 MemberEntity를 명확하게 설정
        if (dto.getMemberId() != null) {
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setMemberNum(dto.getMemberId());
            builder.member(memberEntity);
        }

        return builder.build();
    }

    /**
     * Entity를 DTO로 변환하는 메서드
     * @param entity FundEntity
     * @return FundResponseDto
     */
    private FundResponseDto toDto(FundEntity entity) {
        if (entity == null) {
            return null;
        }

        return FundResponseDto.builder()
                .fundId(entity.getFundId())
                .memberId(entity.getMember() != null ? entity.getMember().getMemberNum() : null)
                .fundSponsor(entity.getFundSponsor())
                .fundPhone(entity.getFundPhone())
                .fundBirth(entity.getFundBirth())
                .fundType(entity.getFundType())
                .fundMoney(entity.getFundMoney())
                .fundTime(entity.getFundTime())
                .fundItem(entity.getFundItem())
                .fundNote(entity.getFundNote())
                .fundBank(entity.getFundBank())
                .fundAccountNum(entity.getFundAccountNum())
                .fundDepositor(entity.getFundDepositor())
                .fundDrawlDate(entity.getFundDrawlDate())
                .fundCheck(entity.getFundCheck())
                .build();
    }


    // 생성
    @PostMapping("/request")
    public ResponseEntity<FundResponseDto> createFund(@Valid @RequestBody FundRequestDto dto) {
        FundResponseDto created = fundService.createFund(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<FundResponseDto> getFund(@PathVariable("id") Long id) {
        FundResponseDto dto = fundService.getFund(id);
        return ResponseEntity.ok(dto);
    }

    // 전체 조회 (페이징, 정렬)
//    @GetMapping("/list")
//    public ResponseEntity<Page<FundResponseDto>> listFunds(
//            @RequestParam(value = "page", defaultValue = "0") int page,
//            @RequestParam(value = "size", defaultValue = "20") int size,
//            @RequestParam(value = "sort", defaultValue = "fundTime,desc") String sort) {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String role = authentication.getAuthorities().stream()
//                .findFirst()
//                .map(a -> a.getAuthority().replace("ROLE_", ""))
//                .orElse("USER");
//
//        // 페이지네이션 및 정렬 설정
//        String[] sortParts = sort.split(",");
//        Sort s = Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]);
//        Pageable pageable = PageRequest.of(page, size, s);
//
//        Page<FundResponseDto> fundPage;
//
//        if ("ADMIN".equals(role)) {
//            fundPage = fundService.getFunds(pageable);
//        } else if ("USER".equals(role)) {
//            String memberId = authentication.getName();
//            // MemberService 주입 필요
//            MemberMeResponseDto member = memberService.getMyInfo(memberId);
//            if (member == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//            }
//            fundPage = fundService.listByMemberNum(member.getMemberNum(), pageable); // Service에 새로 추가해야 함
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        return ResponseEntity.ok(fundPage);
//    }
    @GetMapping("/list")
    public ResponseEntity<Page<FundResponseDto>> listFunds(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "fundTime,desc") String sort) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 익명 사용자일 경우 UNAUTHORIZED 반환 (로그인 상태가 아닐 때)
        if (authentication == null || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("USER");

        String[] sortParts = sort.split(",");
        Sort s = Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]);
        Pageable pageable = PageRequest.of(page, size, s);

        Page<FundResponseDto> fundPage;

        if ("ADMIN".equals(role)) {
            fundPage = fundService.getFunds(pageable);
        } else { // "USER" 역할일 경우
            String memberId = authentication.getName();
            MemberMeResponseDto member = memberService.getMyInfo(memberId);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            fundPage = fundService.listByMemberNum(member.getMemberNum(), pageable);
        }
        return ResponseEntity.ok(fundPage);
    }


    // 스폰서로 검색 (페이징)
    @GetMapping("/search")
    public ResponseEntity<Page<FundResponseDto>> searchBySponsor(
            @RequestParam("sponsor") String sponsor,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fundTime").descending());
        Page<FundResponseDto> results = fundService.searchBySponsor(sponsor, pageable);
        return ResponseEntity.ok(results);
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<FundResponseDto> updateFund(@PathVariable("id") Long id,
            @Valid @RequestBody FundRequestDto dto) {
        FundResponseDto updated = fundService.updateFund(id, dto);
        return ResponseEntity.ok(updated);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFund(@PathVariable("id") Long id) {
        fundService.deleteFund(id);
        return ResponseEntity.noContent().build();
    }
}