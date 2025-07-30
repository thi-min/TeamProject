package com.project.common.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

//JWT 인증 필터
//매 요청마다 실행되며, HTTP 헤더에서 JWT를 추출해 검증
//검증된 경우 SecurityContext에 인증 객체 저장
//Spring Security 필터 체인에 등록되어 동작
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    //JwtTokenProvider 주입 (토큰 검증 및 사용자 정보 추출용)
    public JwtAuthenticationFilter(JwtTokenProvider provider) {
        this.jwtTokenProvider = provider;
    }

     //요청이 들어올 때마다 실행되는 필터 메서드
     //param : request 클라이언트 요청
     //param : response 서버 응답
     //param : chain 필터 체인
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        //1. Authorization 헤더에서 토큰 추출
        String header = request.getHeader("Authorization");

        //2. Bearer 토큰 형식인지 확인
        if (header != null && header.startsWith("Bearer ")) {
            //3. "Bearer " 접두사 제거 후 순수 토큰 값 추출
            String token = header.substring(7);

            //4. 토큰이 유효한 경우에만 처리
            if (jwtTokenProvider.validateToken(token)) {
                //5. 토큰에서 사용자 식별자(memberId) 추출
                String memberId = jwtTokenProvider.getMemberIdFromToken(token);

                //6. 인증 객체 생성 (권한 정보는 비워둠)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(memberId, null, Collections.emptyList());

                //7. SecurityContext에 인증 객체 등록
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        //8. 다음 필터로 전달
        chain.doFilter(request, response);
    }
}
