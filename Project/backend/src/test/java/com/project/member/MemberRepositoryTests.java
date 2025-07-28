package com.project.member;

import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;

import jakarta.transaction.Transactional;

import com.project.member.entity.MemberSex;
import com.project.member.entity.MemberState;
import com.project.admin.entity.AdminEntity;
import com.project.common.util.JasyptUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Commit
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    static {
        System.setProperty("JASYPT_ENCRYPTOR_PASSWORD", "test-key");
    }
    
    @Test
    void findByMemberIdAndMemberPw_정상작동() {
        MemberEntity member = MemberEntity.builder()
        		.memberId("test111@test.com")
                .memberPw("1234")
                .memberName("안길동")
                .memberBirth(LocalDate.of(1996, 5, 3))
                .memberPhone(JasyptUtil.encrypt("22123234545"))
                .memberAddress("서울시 청주구")
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.Man)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .smsAgree(false)
                .build();
        
        memberRepository.save(member);
    }
}
