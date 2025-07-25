package com.project.admin;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Commit
public class AdminRepositoryTests{
	@Autowired
	private AdminRepository adminRepository;
	
	//@Test
    void 테스트데이터_삽입() {
        AdminEntity admin = AdminEntity.builder()
        		.adminId("admin")
                .adminPw("1234")
                .adminName("안형주")
                .adminEmail("admin@test.com")
                .adminPhone("01096861400")
                .registDate(LocalDateTime.now())	//등록일시
                .connectData(LocalDateTime.now())	//접속일시
                .build();

        adminRepository.save(admin);
        Optional<AdminEntity> result = adminRepository.findByAdminIdAndAdminPw("admin","1234");
        assertThat(result).isPresent();
        assertThat(result.get().getAdminName()).isEqualTo("안형주");
	}
}
