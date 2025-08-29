package com.project.admin.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;

import java.time.LocalDateTime;

/**
 * 목적: 애플리케이션 기동 시 관리자 계정이 없으면 자동 생성
 * - 관리자 로그인만 허용(회원가입 없음)
 * - 기본 ID/EMAIL/비밀번호는 운영 환경에서 환경변수로 주입 추천
 */
@Configuration
public class AdminBootstrapConfig {

    // 운영 환경에서는 환경변수/시크릿으로 넘겨주세요.
    private static final String DEFAULT_ADMIN_ID = "admin@admin.kr";
    private static final String DEFAULT_ADMIN_PW = "asd123123"; // 반드시 변경!

    @Bean
    CommandLineRunner adminSeeder(AdminRepository adminRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            boolean exists = adminRepository.findFirstByAdminId(DEFAULT_ADMIN_ID).isPresent();
            if (exists) return;

            AdminEntity admin = AdminEntity.builder()
                    .adminId(DEFAULT_ADMIN_ID)
                    .adminPw(passwordEncoder.encode(DEFAULT_ADMIN_PW)) // ✅ 암호화 저장
                    //.adminPw("asd123123")
                    .adminName("관리자")
                    .adminPhone("01096861400")
                    .registDate(LocalDateTime.now())
                    .connectData(LocalDateTime.now())
                    .build();
            adminRepository.save(admin);
        };
    }
}