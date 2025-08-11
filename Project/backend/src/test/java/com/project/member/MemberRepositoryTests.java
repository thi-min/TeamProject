package com.project.member;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import com.project.common.util.JasyptUtil;
import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberSex;
import com.project.member.entity.MemberState;
import com.project.member.repository.MemberRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Commit
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    //단방향 암호화
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    //복호화 고정키값 and 테스트 키값 ☆필수★
    static {
        System.setProperty("JASYPT_ENCRYPTOR_PASSWORD", "test-key");
    }
    @Test
    void 회원가입() {
    	String encryptedPhone = JasyptUtil.encrypt("01096668888");
        System.out.println("📌 저장용 암호화 값: " + encryptedPhone); // ✅ 이 값이 DB에 저장됨
    	
        MemberEntity member = MemberEntity.builder()
        		.memberId("ahj@test.com")
                .memberPw(passwordEncoder.encode("1234"))
                .memberName("아기아기한 다랑어에요")
                .memberBirth(LocalDate.of(1996, 5, 3))
                .memberPhone(encryptedPhone) //인코딩 예시
                .memberAddress("잠실 종합운동장 포수 후면석")
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.MAN)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .smsAgree(false)
                .build();
        
        memberRepository.save(member);
    }
}
