package com.project.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.member.dto.MemberForcedDeleteDto;
import com.project.member.dto.MemberLoginRequestDto;
import com.project.member.dto.MemberLoginResponseDto;
import com.project.member.dto.MemberMyPageResponseDto;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.member.service.MemberServiceImpl;

@SpringBootTest
class MemberServiceImplTests {

    @Autowired
    private MemberServiceImpl memberService;

    @Autowired
    private MemberRepository memberRepository;

    private Long testMemberNum;

//    @BeforeEach
//    void setUp() {
//        Optional<MemberEntity> member = memberRepository.findByMemberId("test@test.com");
//        if (member.isEmpty()) {
//            throw new RuntimeException("사전 테스트 회원이 존재하지 않습니다.");
//        }
//        testMemberNum = member.get().getMemberNum();
//    }



    @Test
    void 마이페이지_조회_테스트() {
        MemberMyPageResponseDto dto = memberService.myPage(1L);

        assertThat(dto).isNotNull();
        assertThat(dto.getMemberId()).isEqualTo("test@test.com");
        System.out.println(dto.toString());
    }

    //@Test
    void 로그인_성공_테스트() {
        MemberLoginRequestDto loginDto = new MemberLoginRequestDto("test@test.com", "1234");

        MemberLoginResponseDto result = memberService.login(loginDto);

        assertThat(result.getMemberId()).isEqualTo("test@test.com");
        assertThat(result.getMessage()).isEqualTo("로그인 성공");
    }

    //@Test
    void 아이디_찾기_성공_테스트() {
        MemberEntity member = memberRepository.findByMemberNum(testMemberNum).get();

        String result = memberService.findMemberId(member.getMemberName(), member.getMemberPhone());

        assertThat(result).isEqualTo("test@test.com");
    }

   // @Test
    void 비밀번호_찾기_성공_테스트() {
        MemberEntity member = memberRepository.findByMemberNum(testMemberNum).get();

        String result = memberService.findMemberPw(
                member.getMemberId(), member.getMemberName(), member.getMemberPhone()
        );

        assertThat(result).contains("본인 확인");
    }

    //@Test
    void 탈퇴_테스트() {
        MemberForcedDeleteDto result = memberService.memberOut(testMemberNum);

        assertThat(result.getMessage()).isEqualTo("회원 탈퇴 완료");
        assertThat(memberRepository.findByMemberNum(testMemberNum)).isEmpty();
    }

}