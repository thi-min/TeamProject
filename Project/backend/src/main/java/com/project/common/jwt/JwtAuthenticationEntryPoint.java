package com.project.common.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 인증 실패(401) 응답 담당
 * - 토큰 없음/만료/서명 오류 등 인증 자체가 안 된 경우
 * - (선택) JwtAuthenticationFilter가 request attribute에 예외 코드를 넣어주면 메시지에 반영
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 필터에서 넣어줄 키 이름(예: request.setAttribute("jwt_exception", "EXPIRED");)
    public static final String ATTR_JWT_EXCEPTION = "jwt_exception";

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        if (response.isCommitted()) return;

        String reason = (String) request.getAttribute(ATTR_JWT_EXCEPTION); // EXPIRED/INVALID/MISSING 등
        String message;
        if ("EXPIRED".equalsIgnoreCase(reason)) {
            message = "인증 실패: 만료된 토큰입니다.";
        } else if ("INVALID".equalsIgnoreCase(reason)) {
            message = "인증 실패: 유효하지 않은 토큰입니다.";
        } else if ("MISSING".equalsIgnoreCase(reason)) {
            message = "인증 실패: 토큰이 필요합니다.";
        } else {
            message = "인증 실패: 유효하지 않거나 만료된 토큰입니다.";
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");
        // OAuth2 호환 헤더(디버깅·클라이언트 라이브러리 친화)
        response.setHeader("WWW-Authenticate",
                "Bearer realm=\"api\", error=\"invalid_token\", error_description=\"" + message + "\"");

        String body = String.format(
                "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\",\"path\":\"%s\"}",
                LocalDateTime.now(), message, request.getRequestURI()
        );
        response.getWriter().write(body);
    }
}
