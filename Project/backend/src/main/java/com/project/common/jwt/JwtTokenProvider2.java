//package com.project.common.jwt;
//
//import java.security.Key;
//import java.util.Date;
//
//import org.springframework.stereotype.Component;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//
//@Component
//public class JwtTokenProvider2 {
//
//    // JWT 비밀키
//    private String secretKey = "VerySecretKeyForJwtSigningThatIsSecureAndLongEnough";
//    private Key key;
//
//    // 토큰 유효 시간 (단위: ms)
//    private final long accessTokenValidity = 1000 * 60 * 30;     // 30분
//    private final long refreshTokenValidity = 1000 * 60 * 60 * 24 * 7; // 7일
//
//    // 🔐 key 초기화
//    @PostConstruct
//    protected void init() {
//        key = Keys.hmacShaKeyFor(secretKey.getBytes());
//    }
//
//    // ==============================
//    // ✅ [1] 회원용 Access Token 생성
//    // ==============================
//    // param : memberId - 사용자 식별자
//    // return : JWT 문자열 (role은 기본값 USER)
//    public String generateAccessToken(String subject) {
//        return generateAccessToken(subject, "USER"); // 기본은 USER 역할
//    }
//
//    // ==============================
//    // ✅ [2] 역할 지정용 Access Token 생성 (관리자용 포함)
//    // ==============================
//    // param : id - 사용자 또는 관리자 식별자
//    // param : role - 역할 (ex: "USER", "ADMIN")
//    // return : JWT 문자열
//    public String generateAccessToken(String subject, String role) {
//        Date now = new Date();
//        Date expiry = new Date(now.getTime() + accessTokenValidity); // 유효 시간 설정
//
//        // JWT Claims 설정
//        Claims claims = Jwts.claims().setSubject(subject); // 필수: 식별자
//        claims.put("role", role); // ✅ 역할 정보 포함
//
//        return Jwts.builder()
//        			.setSubject(subject)
//                .setIssuedAt(now) // 발급 시간
//                .setExpiration(expiry) // 만료 시간
//                .signWith(key, SignatureAlgorithm.HS256) // 서명
//                .compact(); // 최종 토큰 문자열 생성
//    }
//
//    // ==============================
//    // ✅ [3] Refresh Token 생성 (공통)
//    // ==============================
//    // param : id - 사용자 또는 관리자 식별자
//    // return : 서명된 JWT 문자열
//    public String generateRefreshToken(String id) {
//        Date now = new Date();
//        Date expiry = new Date(now.getTime() + refreshTokenValidity); // 유효 시간 설정
//
//        return Jwts.builder()
//                .setSubject(id)
//                .setIssuedAt(now)
//                .setExpiration(expiry)
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    // ==============================
//    // ✅ [4] 토큰에서 사용자 ID 추출
//    // ==============================
//    public String getMemberIdFromToken(String token) {
//        return Jwts.parserBuilder().setSigningKey(key).build()
//                .parseClaimsJws(token).getBody().getSubject();
//    }
//
//    // ==============================
//    // ✅ [5] 토큰에서 역할(Role) 추출
//    // ==============================
//    public String getRoleFromToken(String token) {
//        return (String) Jwts.parserBuilder().setSigningKey(key).build()
//                .parseClaimsJws(token).getBody().get("role");
//    }
//
//    // ==============================
//    // ✅ [6] 토큰 유효성 검증
//    // ==============================
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (JwtException | IllegalArgumentException e) {
//            return false; // 유효하지 않은 토큰
//        }
//    }
//}
