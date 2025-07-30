package com.project.admin;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
import com.project.common.util.JasyptUtil;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Commit
public class AdminRepositoryTests{
	@Autowired
	private AdminRepository adminRepository;
	//단방향 암호화
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	//복호화 고정키값 and 테스트 키값 ☆필수★
    static {
        System.setProperty("JASYPT_ENCRYPTOR_PASSWORD", "test-key");
    }
	@Test
    void 테스트데이터_삽입() {
    	String encryptedPhone = JasyptUtil.encrypt("01096861400");
    	System.out.println("📌 저장용 암호화 값: " + encryptedPhone);
    	
        AdminEntity admin = AdminEntity.builder()
        		.adminId("admin2")
                .adminPw(passwordEncoder.encode("passs123"))
                .adminName("안형주")
                .adminEmail("admin@test.com")
                .adminPhone(JasyptUtil.encrypt("01096861400"))
                .registDate(LocalDateTime.now())	//등록일시
                .connectData(LocalDateTime.now())	//접속일시
                .build();

        adminRepository.save(admin);
        //Optional<AdminEntity> result = adminRepository.findByAdminIdAndAdminPw("admin","1234");
        //assertThat(result).isPresent();
        //assertThat(result.get().getAdminName()).isEqualTo("안형주");
	}
}
