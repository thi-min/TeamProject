package com.project.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.project.common.jwt.JwtTokenProvider;
import com.project.common.util.JasyptUtil;
import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberState;
import com.project.member.repository.MemberRepository;

import io.jsonwebtoken.Jwts;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    private String memberId = "jwtuser@test.com";

    //복호화 고정키값 and 테스트 키값 ☆필수★
    static {
        System.setProperty("JASYPT_ENCRYPTOR_PASSWORD", "test-key");
    }
    //@BeforeEach
    void setUp() {
        // 🧪 테스트용 사용자 등록
        Optional<MemberEntity> exist = memberRepository.findByMemberId(memberId);
        if (exist.isEmpty()) {
            MemberEntity testMember = MemberEntity.builder()
                    .memberId(memberId)
                    .memberPw("1234") // 비밀번호는 로그인에 필요 없음
                    .memberName("JWT Tester")
                    .memberPhone(JasyptUtil.encrypt("01055565400"))
                    .memberAddress("중국일본아시아남아메리카레쓰고")
                    .memberBirth(LocalDate.of(1996, 5, 3))
                    .memberDay(LocalDate.now())
                    .memberState(MemberState.ACTIVE)
                    .build();

            memberRepository.save(testMember);
        }
    }

    //@Test
    @DisplayName("🔐 토큰 없이 인증된 엔드포인트 접근 시 401 반환")
    void 접근_토큰_없음_401() throws Exception {
        mockMvc.perform(get("/member/mypage/1")) // 인증 필요 엔드포인트
                .andExpect(status().isUnauthorized()) // 401
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("인증 실패: 유효하지 않거나 만료된 토큰입니다."));
    }

    @Test
    @DisplayName("🧨 만료된 토큰으로 접근 시 401 반환")
    void 만료된_토큰_테스트() throws Exception {
        // 📌 유효시간을 1ms로 설정해서 강제로 만료된 토큰 생성
        String expiredToken = Jwts.builder()
                .setSubject(memberId)
                .setIssuedAt(new java.util.Date(System.currentTimeMillis() - 10000))
                .setExpiration(new java.util.Date(System.currentTimeMillis() - 5000)) // 이미 만료됨
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        "your-very-secure-jwt-secret-key-should-be-long".getBytes()), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

        mockMvc.perform(get("/member/mypage/1")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("인증 실패: 유효하지 않거나 만료된 토큰입니다."));
    }

    //@Test
    @DisplayName("🚪 로그아웃 시 RefreshToken 제거")
    void 로그아웃_성공() throws Exception {
        String refreshToken = jwtTokenProvider.generateRefreshToken(memberId);

        // 🔐 토큰을 DB에 저장
        MemberEntity member = memberRepository.findByMemberId(memberId).get();
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);

        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isOk())
                .andExpect(content().string("로그아웃 완료"));

        MemberEntity updated = memberRepository.findByMemberId(memberId).get();
        assertThat(updated.getRefreshToken()).isNull();
    }
}
