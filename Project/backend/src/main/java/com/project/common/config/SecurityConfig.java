// ✅ 파일: SecurityConfig.java
// ✅ 목적: CORS 허용, /auth/** 공개, JWT 필터 유지

package com.project.common.config;

import com.project.common.jwt.JwtAuthenticationEntryPoint;
import com.project.common.jwt.JwtAuthenticationFilter;
import com.project.common.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

// ⬇️ CORS 관련 import 추가
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ✅ CORS 활성화 (아래 corsConfigurationSource() 사용)
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
                // ⛔️ "/**" 전체 허용은 지양. 필요한 공개 엔드포인트만 지정
                .requestMatchers(
                    "/**",         // 로그인, 회원가입, 아이디체크 등
                    "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
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
        config.setAllowCredentials(false);
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
