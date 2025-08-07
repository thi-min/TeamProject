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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    //Spring Security 필터 체인 정의
    //JWT 인증 방식에 맞춰 Stateless 설정
    //인증 필터 수동 등록
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            //CSRF 보호 비활성화 (JWT는 세션을 사용하지 않으므로 필요 없음)
            .csrf(AbstractHttpConfigurer::disable)

            //세션 생성 정책 설정: STATELESS -> 세션 사용하지 않음
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            //인증 실패 핸들러 등록
            .exceptionHandling(exception -> exception
            		.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            
            //URL 접근 권한 설정(토큰없이 접근가능)
            .authorizeHttpRequests(auth -> auth
                //.requestMatchers("/swagger-ui/**","/v3/api-docs/**","/swagger-resources/**","/webjars/**").permitAll()
                //사용자 인증 API 허용
                .requestMatchers("/**","/signup","/auth/login", "/auth/signup", "/auth/**").permitAll()
                .anyRequest().authenticated()  //그 외 요청은 인증 필요
            )

            //커스텀 JWT 필터 등록 (UsernamePasswordAuthenticationFilter 이전에 실행)
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                             org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)

            //폼 로그인, 기본 로그인 페이지 비활성화 (REST API에 필요 없음)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable);
        	
        
        	
        return http.build();
    }

    //AuthenticationManager가 필요한 경우 정의 가능 (예: 비밀번호 인증 등)
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManager.class);
    }
}
