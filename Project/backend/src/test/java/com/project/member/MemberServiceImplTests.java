package com.project.member;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
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

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class MemberServiceImplTests {

    @Autowired
    private MemberServiceImpl memberService;

    @Autowired
    private MemberRepository memberRepository;

    private Long testMemberNum;

    /**
     * 각 테스트 실행 전 공통 준비 작업 수행
     * - 테스트용 회원 ID("test@test.com")로 memberNum 조회하여 저장
     * - 테스트 데이터는 이미 DB에 있다고 가정
     */
    @BeforeEach
    void setUp() {
        Optional<MemberEntity> member = memberRepository.findByMemberId("test@test.com");
        testMemberNum = member.map(MemberEntity::getMemberNum)
                              .orElseThrow(() -> new RuntimeException("사전 테스트 회원이 존재하지 않습니다."));
    }

    /**
     * 마이페이지 조회 테스트
     * - 회원 번호로 조회했을 때, 회원 정보가 정상적으로 반환되는지 검증
     */
    @Test
    void 마이페이지_조회_테스트() {
        MemberMyPageResponseDto dto = memberService.myPage(testMemberNum);

        assertThat(dto).isNotNull();
        assertThat(dto.getMemberId()).isEqualTo("test@test.com");
    }

    /**
     * 로그인 성공 테스트
     * - 올바른 아이디와 비밀번호로 로그인했을 때, DTO가 정상 반환되는지 검증
     */
    @Test
    void 로그인_성공_테스트() {
        MemberLoginRequestDto loginDto = new MemberLoginRequestDto("test@test.com", "1234");

        MemberLoginResponseDto result = memberService.login(loginDto);

        assertThat(result.getMemberId()).isEqualTo("test@test.com");
        assertThat(result.getMessage()).isEqualTo("로그인 성공");
    }

    /**
     * 아이디 찾기 테스트
     * - 이름 + 전화번호로 회원의 아이디(email)가 정상 조회되는지 확인
     */
    @Test
    void 아이디_찾기_성공_테스트() {
        MemberEntity member = memberRepository.findByMemberNum(testMemberNum).get();

        String result = memberService.findMemberId(member.getMemberName(), member.getMemberPhone());

        assertThat(result).isEqualTo("test@test.com");
    }

    /**
     * 비밀번호 찾기 테스트
     * - 아이디 + 이름 + 전화번호로 본인 확인이 정상적으로 되는지 검증
     */
    @Test
    void 비밀번호_찾기_성공_테스트() {
        MemberEntity member = memberRepository.findByMemberNum(testMemberNum).get();

        String result = memberService.findMemberPw(
                member.getMemberId(), member.getMemberName(), member.getMemberPhone()
        );

        assertThat(result).contains("본인 확인");
    }

    /**
     * 회원 탈퇴 테스트
     * - 회원 번호로 회원을 삭제하고, 삭제 후 다시 조회했을 때 존재하지 않아야 함
     */
    @Test
    void 탈퇴_테스트() {
        MemberForcedDeleteDto result = memberService.memberOut(testMemberNum);

        assertThat(result.getMessage()).isEqualTo("회원 탈퇴 완료");
        assertThat(memberRepository.findByMemberNum(testMemberNum)).isEmpty();
    }

}
