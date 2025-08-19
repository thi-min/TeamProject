// ✅ 파일: SecurityConfig.java
// ✅ 목적: CORS 허용, /auth/** 공개, JWT 필터 유지

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
// ⬇️ CORS 관련 import 추가
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
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        // 필터가 JwtTokenProvider를 생성자에서 받는다고 가정
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ✅ CORS 활성화 (아래 corsConfigurationSource() Bean 사용)
            .cors(Customizer.withDefaults())

            // ✅ JWT 사용 시 CSRF 비활성화 (중복 설정 제거)
            .csrf(AbstractHttpConfigurer::disable)

            // ✅ 세션 미사용
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ✅ 인증 실패(401) 응답 포맷 커스터마이징
            .exceptionHandling(e -> e
            		.authenticationEntryPoint(jwtAuthenticationEntryPoint)	//401 처리
            		.accessDeniedHandler(jwtAccessDeniedHandler))        // ✅ 403 처리 추가
            // ✅ 요청별 인가 규칙
            .authorizeHttpRequests(auth -> auth
                // 공개 엔드포인트 (로그인/재발급)
                .requestMatchers("/auth/login", "/auth/reissue").permitAll()

                // (선택) 스웨거 열어두기
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // 관리자 전용
                .requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")
                // 나머지는 필요에 따라 조정
                .anyRequest().permitAll()
            )

            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // ✅ 전역 CORS 설정 (localhost:3000에서 오는 요청 허용 + 프리플라이트(OPTIONS) 허용)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 프론트 개발 도메인 허용 (필요 시 127.0.0.1도 추가)
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));
        // 모든 메서드 허용 (GET/POST/PUT/DELETE/OPTIONS)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // 모든 헤더 허용
        config.setAllowedHeaders(List.of("*"));
        // 인증 쿠키/헤더를 쓸 경우 true (지금은 false로도 OK)
        config.setAllowCredentials(true);
        // (선택) 프론트에서 읽어야 하는 커스텀 헤더가 있다면 노출
        // config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 전체 경로에 CORS 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManager.class);
    }
}
