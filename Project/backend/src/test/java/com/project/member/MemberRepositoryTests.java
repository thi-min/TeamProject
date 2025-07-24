package com.project.member;

import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.member.entity.MemberSex;
import com.project.member.entity.MemberState;
import com.project.admin.entity.AdminEntity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void findByMemberIdAndMemberPw_정상작동() {
        MemberEntity member = MemberEntity.builder()
        		.memberId("test@test.com")
                .memberPw("1234")
                .memberName("홍길동")
                .memberBirth(LocalDate.of(1996, 1, 1))
                .memberPhone("01012345678")
                .memberAddress("서울시 강남구")
                .memberDay(LocalDate.now())
                .memberSex(MemberSex.Man)
                .memberState(MemberState.ACTIVE)
                .memberLock(false)
                .snsYn(false)
                .build();

        memberRepository.save(member);

        Optional<MemberEntity> result = memberRepository.findByMemberIdAndMemberPw("test@test.com", "1234");

        assertThat(result).isPresent();
        assertThat(result.get().getMemberName()).isEqualTo("홍길동");
    }
}
