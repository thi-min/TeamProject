package com.project.common.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

//토큰만료 예외 핸들링
//인증실패(401) 발생 시 처리할 진입점
//만료된 토큰
//헤더 없음
//유효하지 않는 서명
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{
	
	@Override
	public void commence(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException authException
	)throws IOException, ServletException{
		//응답코드 401 설정 + 메시지 반환
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().write("{\"error\": \"인증 실패: 유효하지 않거나 만료된 토큰입니다.\"}");
	}
}
