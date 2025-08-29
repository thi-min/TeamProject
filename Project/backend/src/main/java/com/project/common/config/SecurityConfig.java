// 파일: backend/src/main/java/com/project/common/config/SecurityConfig.java
// 목적:
//  - 전역 보안 설정(Spring Security 6.x)
//  - ✅ CORS 허용(프론트: 127.0.0.1/localhost:3000)
//  - ✅ /auth/** 공개(로그인/재발급)
//  - ✅ /kakao/callback 공개(카카오 로그인 콜백 처리용) ← 이번 작업 핵심
//  - ✅ JWT 필터 유지(요청 전에 토큰 검증)
//  - ✅ 관리자 경로는 권한 필요(/admin/**)
//
// 변경 요약(이번 작업):
//  1) .requestMatchers(HttpMethod.GET, "/kakao/callback").permitAll() 추가
//  2) 나머지 기존 설정(CORS/CSRF/JWT 필터/예외 핸들러/세션 정책) 유지
//
// 주의:
//  - anyRequest().permitAll() 이 존재하므로, /admin/** 권한 체크는 위에서 먼저 매칭되므로 정상 동작합니다.
//  - 운영 환경에서는 anyRequest().authenticated() 로 바꾸고 공개 경로만 명시하는 방식 권장

package com.project.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// ⬇️ CORS 관련 import
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.project.common.jwt.JwtAccessDeniedHandler;
import com.project.common.jwt.JwtAuthenticationEntryPoint;
import com.project.common.jwt.JwtAuthenticationFilter;
import com.project.common.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * JWT 인증 필터 Bean
     * - 각 요청 전에 Authorization 헤더의 Bearer 토큰을 파싱/검증
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    /**
     * 메인 보안 체인 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ✅ CORS 활성화 (아래 corsConfigurationSource() Bean 사용)
            .cors(Customizer.withDefaults())

            // ✅ JWT 사용 시 CSRF 비활성화
            .csrf(AbstractHttpConfigurer::disable)

            // ✅ 세션 미사용(STATELESS)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ✅ 인증/인가 예외 핸들러
            .exceptionHandling(e -> e
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 401
                .accessDeniedHandler(jwtAccessDeniedHandler)           // 403
            )

            // ✅ 요청별 인가 규칙
            .authorizeHttpRequests(auth -> auth
                // 공개 엔드포인트 (로그인/재발급)
                .requestMatchers("/auth/login", "/auth/reissue").permitAll()

                // ✅ 카카오 로그인 콜백 (프론트가 code 들고 호출)
                .requestMatchers(HttpMethod.GET, "/kakao/callback").permitAll()

                // 관리자 전용
                .requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                // 나머지는 정책에 맞게 조정
                .anyRequest().permitAll()
            )

            // ✅ JWT 필터를 UsernamePasswordAuthenticationFilter 전에 실행
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

            // 기본 인증/폼 로그인 비활성화
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * 전역 CORS 설정
     * - 프론트 개발 도메인 허용 (localhost/127.0.0.1:3000)
     * - 자격 증명 사용(true) → AllowedOrigins에 * 사용 불가(정확한 오리진 명시)
     * - OPTIONS 프리플라이트 허용(AllowedMethods에 OPTIONS 포함)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 프론트 개발 도메인
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://127.0.0.1:3000"
        ));

        // 모든 메서드 허용
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 모든 헤더 허용
        config.setAllowedHeaders(List.of("*"));

        // 쿠키/인증 헤더 허용 여부 (필요 시 true)
        config.setAllowCredentials(true);

        // (선택) 프론트에서 읽어야 하는 커스텀 헤더 노출
        // config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 전체 경로에 CORS 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * AuthenticationManager 빈 노출
     * - 일부 컴포넌트가 AuthenticationManager 주입을 필요로 하는 경우 사용
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManager.class);
    }
}
