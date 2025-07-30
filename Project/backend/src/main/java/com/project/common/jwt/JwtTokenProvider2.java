package com.project.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;

//JWT 토큰을 생성, 파싱, 검증하는 컴포턴트 클래스
@Component
public class JwtTokenProvider2 {
	//알고리즘에 사용할 비밀 키
	private final Key key = Keys.hmacShaKeyFor("mySuperSecretKeyThatIsLongEnoughs".getBytes());
	//토큰 유효시간
	private final long accessTokenValidity = 1000L * 60 * 30;	//30분
	private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 7; //7일
	
	//JWT 토큰 생성 메서드
	//param : memberId토큰에 포함할 사용자 식별자
	//return : 생성된 JWT문자열
	public String generateAccessToken(String memberId) {
		//토큰 발급시간
		Date now = new Date();
		//토큰 만료시간
		Date expiry = new Date(now.getTime() + accessTokenValidity);	//유효기간 30분
		
		return Jwts.builder()
				.setSubject(memberId) 			//사용자 식별수(필수)
				.setIssuedAt(now)				//토큰 발급 시간
				.setExpiration(expiry)			//토큰 만료 시간
				.signWith(key, SignatureAlgorithm.HS256)	//서명
				.compact(); 					//최종 토큰 문자열 생성
	}
	
	//Refresh Token 생성(Access Token 재발급용)
	//param : memberId 사용자 식별자
	//return : 서명된 JWT 리프레시 토큰
	public String generateRefreshToken(String memberId) {
		//토큰 발급시간
		Date now = new Date();
		//토큰 만료시간
		Date expiry = new Date(now.getTime() + refreshTokenValidity);	//유효기간 7일
		
		return Jwts.builder()
				.setSubject(memberId) 			//사용자 식별수(필수)
				.setIssuedAt(now)				//토큰 발급 시간
				.setExpiration(expiry)			//토큰 만료 시간
				.signWith(key, SignatureAlgorithm.HS256)	//서명
				.compact(); 
	}
	
	//JWT 토큰에서 사용자 식별자(memberID)를 추출
	//param : token 클라이언트로부터 받은 JWT
	//return : 토큰의 subject(memberId)
	public String getMemberIdFromToken(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(key)		//서명 검증을 위한 키
				.build()
				.parseClaimsJws(token) 	//토큰 파싱
				.getBody()
				.getSubject();			//subject 추출
	}
	
	//JWT 유효성 검사 메서드
	//param : token 클라이언트로부터 받은 JWT
	//return : 유효하면 true, 만료 또는 위조되었으면 false
	public boolean validateToken(String token) {
		try {
			//서명 확인 + 파싱
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;
		}catch(JwtException | IllegalArgumentException e) {
			//위조, 만료, 형식, 오류 등 모든 예외를 false처리
			return false;
		}
	}
	
	//JWT 토큰 생성 시 memberId와 역할(role)를 포함하도록 수정
	public String createToken(String memberId, String role) {
		Claims claims = Jwts.claims().setSubject(memberId);		//사용자 ID 저장
		claims.put("role",role);		//역할 정보 추가

		LocalDateTime now = LocalDateTime.now();
		Date issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
		Date expiry = Date.from(now.plusHours(1).atZone(ZoneId.systemDefault()).toInstant());
		
		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(issuedAt)
				.setExpiration(expiry)
				.signWith(SignatureAlgorithm.HS256, key)
				.compact();
	}
}
