package com.project.admin.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.admin.dto.*;
import com.project.admin.entity.AdminEntity;
import com.project.admin.entity.AdminInviteToken;
import com.project.admin.repository.AdminInviteTokenRepository;
import com.project.admin.repository.AdminRepository;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminInviteServiceImpl implements AdminInviteService {

    private final AdminInviteTokenRepository inviteRepo;
    private final AdminRepository adminRepo;
    private final MemberRepository memberRepo; // Member 연동 시 사용
    private final PasswordEncoder passwordEncoder;

    // ===== 초대 생성 =====
    @Override
    @Transactional
    public AdminInviteCreateResponseDto createInvite(String issuedBy, AdminInviteCreateRequestDto req) {
        int ttl = (req.getTtlMinutes() == null || req.getTtlMinutes() <= 0) ? 30 : req.getTtlMinutes();
        String token = generateUniqueToken();

        AdminInviteToken invite = AdminInviteToken.builder()
                .token(token)
                .targetEmail(req.getTargetEmail())
                .issuedBy(issuedBy)
                .expiresAt(LocalDateTime.now().plusMinutes(ttl))
                .memo(req.getMemo())
                .build();

        inviteRepo.save(invite);

        return AdminInviteCreateResponseDto.builder()
                .token(token)
                .expiresAt(invite.getExpiresAt())
                .build();
    }

    // ===== 초대 검증(내부용) =====
    @Override
    public AdminInviteToken getValidInviteOrThrow(String token) {
        AdminInviteToken inv = inviteRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 토큰입니다."));
        if (inv.isUsed()) throw new IllegalStateException("이미 사용된 초대 토큰입니다.");
        if (inv.isExpired()) throw new IllegalStateException("만료된 초대 토큰입니다.");
        return inv;
    }

    // ===== 초대 검증(외부 응답) =====
    @Override
    public AdminInviteVerifyResponseDto verifyInvite(String token) {
        try {
            AdminInviteToken inv = getValidInviteOrThrow(token);
            return AdminInviteVerifyResponseDto.builder()
                    .valid(true)
                    .targetEmail(inv.getTargetEmail())
                    .expiresAt(inv.getExpiresAt())
                    .message("초대 토큰이 유효합니다.")
                    .build();
        } catch (Exception e) {
            return AdminInviteVerifyResponseDto.builder()
                    .valid(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    // ===== 초대 수락(=관리자 회원가입) =====
    @Override
    @Transactional
    public AdminSignupByInviteResponseDto acceptInvite(AdminSignupByInviteRequestDto req) {
        // 1) 토큰 유효성
        AdminInviteToken inv = getValidInviteOrThrow(req.getToken());

        // 2) 토큰 이메일과 입력 이메일 일치(권장)
        if (!inv.getTargetEmail().equalsIgnoreCase(req.getAdminEmail())) {
            throw new IllegalArgumentException("초대 이메일과 입력 이메일이 일치하지 않습니다.");
        }

        // 3) 중복 관리자 ID/이메일 체크
        if (adminRepo.findFirstByAdminId(req.getAdminId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 관리자 아이디입니다.");
        }
        if (adminRepo.findFirstByAdminEmail(req.getAdminEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 관리자 이메일입니다.");
        }

        // 4) Member 연동이 필요한 경우
        MemberEntity member = null;
        if (req.getMemberNum() != null) {
            member = memberRepo.findById(req.getMemberNum())
                    .orElseThrow(() -> new IllegalArgumentException("연동할 회원을 찾을 수 없습니다."));
        }

        // 5) 비밀번호 암호화
        String encodedPw = passwordEncoder.encode(req.getAdminPw());

        // 6) 관리자 엔티티 생성/저장
        AdminEntity admin = AdminEntity.builder()
                .adminId(req.getAdminId())
                .adminEmail(req.getAdminEmail())
                .adminPw(encodedPw)
                .adminName(req.getAdminName())
                .adminPhone(req.getAdminPhone())
                .member(member) // null 허용
                .registDate(LocalDateTime.now())
                .connectData(LocalDateTime.now())
                .build();
        adminRepo.save(admin);

        // 7) 초대 토큰 사용 처리(1회용)
        inv.markUsed();
        inviteRepo.save(inv);

        // 8) 응답
        return AdminSignupByInviteResponseDto.builder()
                .adminId(admin.getAdminId())
                .adminEmail(admin.getAdminEmail())
                .message("관리자 회원가입이 완료되었습니다.")
                .build();
    }

    // ===== 토큰 생성 유틸 =====
    private String generateUniqueToken() {
        SecureRandom random = new SecureRandom();
        byte[] buf = new byte[32]; // 256bit
        String token;
        do {
            random.nextBytes(buf);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
        } while (inviteRepo.existsByToken(token));
        return token;
    }
}
