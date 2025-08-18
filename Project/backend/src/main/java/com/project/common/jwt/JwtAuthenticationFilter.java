package com.project.common.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 목적: JWT에서 주체(subject)와 권한(role/roles/authorities)을 읽어
 *      Spring Security Authentication 에 주입.
 * 포인트:
 *  - 토큰 클레임에 "ADMIN" 또는 "ROLE_ADMIN" 등 무엇이 오든 간에,
 *    Authentication에는 최소 하나의 권한을 "ADMIN" 으로 '정규화' 하여 넣는다.
 *  - 이렇게 하면 보안 규칙에서 hasAuthority("ADMIN")만 검사하면 됨.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider; // 이미 존재한다고 가정

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            if (!jwtTokenProvider.validateToken(token)) {
                chain.doFilter(request, response);
                return;
            }

            Claims claims = jwtTokenProvider.getClaims(token); // 구현체에 맞게 조정
            String subject = claims.getSubject();              // adminId 또는 memberId

            // role 추출 ("ADMIN" / "ROLE_ADMIN" / "USER" ...)
            Object raw = claims.get("role");
            if (raw == null) raw = claims.get("roles");
            if (raw == null) raw = claims.get("authorities");

            String roleString = (raw == null) ? "" : String.valueOf(raw).toUpperCase();

            // 2) "ROLE_ADMIN" → "ADMIN" 으로 정규화
            if (roleString.startsWith("ROLE_")) {
                roleString = roleString.substring(5);
            }

            // 3) 최종 권한 리스트 구성
            //    - ADMIN 이면 ADMIN 권한 부여
            //    - USER 등 다른 값이면 해당 값 부여(선택) + ADMIN은 부여하지 않음
            GrantedAuthority auth;
            if ("ADMIN".equals(roleString)) {
                auth = new SimpleGrantedAuthority("ADMIN");
            } else {
                // 필요 시 여러 권한 파싱 로직 추가 가능
                auth = new SimpleGrantedAuthority(roleString.isEmpty() ? "USER" : roleString);
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(subject, null, List.of(auth));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            // 유효하지 않은 토큰 등: 그냥 다음 필터로 넘기고 EntryPoint가 401 처리
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
