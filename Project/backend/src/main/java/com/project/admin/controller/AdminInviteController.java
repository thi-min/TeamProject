package com.project.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.admin.dto.AdminInviteCreateRequestDto;
import com.project.admin.dto.AdminInviteCreateResponseDto;
import com.project.admin.dto.AdminInviteVerifyResponseDto;
import com.project.admin.dto.AdminSignupByInviteRequestDto;
import com.project.admin.dto.AdminSignupByInviteResponseDto;
import com.project.admin.service.AdminInviteService;

import lombok.RequiredArgsConstructor;

/**
 * 관리자 초대/가입 컨트롤러
 * - 생성:     POST   /admin/invites            (슈퍼관리자 권한 필요)
 * - 검증:     GET    /admin/invites/{token}    (공개)
 * - 초대 수락: POST  /adminS/invites/accept     (공개: 토큰 기반)
 */
@RestController
@RequestMapping("/admin/invites")
@RequiredArgsConstructor
public class AdminInviteController {

    private final AdminInviteService inviteService;
    // (슈퍼관리자 전용) 초대 생성
    @PostMapping
    public ResponseEntity<AdminInviteCreateResponseDto> create(@RequestHeader("X-Admin-Id") String issuedBy,
                                                            @RequestBody AdminInviteCreateRequestDto req) {
        // 실제로는 SecurityContext에서 발급자 id 꺼내는 식으로 변경
        return ResponseEntity.ok(inviteService.createInvite(issuedBy, req));
    }

    // 초대 토큰 검증(공개)
    @GetMapping("/{token}")
    public ResponseEntity<AdminInviteVerifyResponseDto> verify(@PathVariable String token) {
        return ResponseEntity.ok(inviteService.verifyInvite(token));
    }

    // 초대 수락(=관리자 회원가입, 공개)
    @PostMapping("/accept")
    public ResponseEntity<AdminSignupByInviteResponseDto> accept(@RequestBody AdminSignupByInviteRequestDto req) {
        return ResponseEntity.ok(inviteService.acceptInvite(req));
    }
}
