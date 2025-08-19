package com.project.common.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * JWT 발급/검증 유틸리티
 * - AccessToken: 30분(기본), RefreshToken: 7일(기본)
 * - subject에는 "이메일(=ID)"를 넣는 것을 전제로 함 (회원/관리자 공통)
 * - role 클레임: "USER" or "ADMIN" (서버에서 판단하여 발급)
 */
@Component
public class JwtTokenProvider {

    // =====================================
    // 🔐 비밀키 설정
    //  - 운영 환경에서는 application.yml or 환경변수로 주입 권장
    //  - HS256 사용 시, 최소 256bit(32바이트) 이상의 키 권장
    // =====================================
    @Value("${jwt.secret:VerySecretKeyForJwtSigningThatIsSecureAndLongEnough}")
    private String secretKeyRaw;

    private Key key; // 서명키 (HMAC)

    // =====================================
    // ⏱️ 토큰 유효 시간 (ms)
    // =====================================
    @Value("${jwt.access-validity-ms:1800000}")      // 기본 30분
    private long accessTokenValidityMs;

    @Value("${jwt.refresh-validity-ms:604800000}")   // 기본 7일
    private long refreshTokenValidityMs;

    // 🔐 key 초기화
    @PostConstruct
    protected void init() {
        // 문자열 키를 그대로 바이트로 사용 (운영에서는 Base64 디코딩 등 사용 고려)
        byte[] keyBytes = secretKeyRaw.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // ==============================
    // ✅ [1] 회원용 Access Token 생성(기본 role=USER)
    // ==============================
    // param : subject - 사용자 식별자(이메일=ID)
    // return: JWT 문자열
    public String generateAccessToken(String subject) {
        return generateAccessToken(subject, "USER"); // 기본은 USER 역할
    }

    // ==============================
    // ✅ [2] 역할 지정용 Access Token 생성 (관리자 포함)
    // ==============================
    // param : subject - 사용자/관리자 식별자(이메일=ID)
    // param : role    - "USER" or "ADMIN"
    // return: JWT 문자열
    public String generateAccessToken(String subject, String role) {
        final Date now = new Date();
        final Date expiry = new Date(now.getTime() + accessTokenValidityMs);

        // 🎯 클레임 구성
        Claims claims = Jwts.claims().setSubject(subject); // sub = 이메일(=ID)
        claims.put("role", role);                          // ✅ 역할 정보 포함

        // ⚠️ 기존 코드에서는 claims를 만들고 빌더에 set하지 않았음 → 아래에서 setClaims로 반영
        return Jwts.builder()
                .setClaims(claims)                 // ← 반드시 claims 반영
                .setIssuedAt(now)                  // 발급 시간
                .setExpiration(expiry)             // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 서명
                .compact();                        // 최종 토큰
    }

    // ==============================
    // ✅ [3] Refresh Token 생성 (공통)
    // ==============================
    // param : subject - 사용자/관리자 식별자(이메일=ID)
    // return: JWT 문자열
    public String generateRefreshToken(String subject) {
        final Date now = new Date();
        final Date expiry = new Date(now.getTime() + refreshTokenValidityMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ==============================
    // ✅ [4] 토큰에서 사용자 ID(subject) 추출
    // ==============================
    public String getMemberIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ==============================
    // ✅ [5] 토큰에서 역할(Role) 추출
    // ==============================
    public String getRoleFromToken(String token) {
        Object role = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
        return role == null ? null : role.toString();
    }

    // ==============================
    // ✅ [6] 토큰 유효성 검증(서명/만료)
    // ==============================
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 로그를 남기고 false 반환 (서명 위변조/만료/형식 오류 등)
            return false;
        }
    }
    
    //토큰에서 Claims 전부 꺼내기
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
