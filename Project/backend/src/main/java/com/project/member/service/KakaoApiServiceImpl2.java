//// 목적: 카카오 OAuth2 연동 서비스 구현체
////  - 인가코드(code)로 access_token 교환
////  - access_token 으로 사용자 정보 조회
////
//// ✅ 이번 수정 핵심
////  1) redirect_uri 하드코딩 제거 → application.properties에서 주입(@Value)
////     - 카카오 인가요청에 사용한 redirect_uri와 "완전히 동일"해야 토큰 교환이 성공합니다.
////  2) 토큰 요청 시 헤더/파라미터 보강 (Accept: application/json 권장)
////  3) 사용자 정보 파싱 가드 보강(null-safe)
////  4) (선택) 전화번호 문자열 정규화 유틸 추가
////
//// 📌 사전 확인 (application.properties)
////  kakao.client-id=81e534db4230445c24fa35d7ac6594af
////  kakao.redirect-uri=http://127.0.0.1:3000/oauth/kakao/callback   ← 주석 해제 및 프론트 .env와 동일!
////  kakao.token-uri=https://kauth.kakao.com/oauth/token
////  kakao.user-info-uri=https://kapi.kakao.com/v2/user/me
////
//// ⚠️ redirect_uri 주의
////  - 프론트의 .env(REACT_APP_KAKAO_REDIRECT_URI)와 "완전히 동일"해야 합니다.
////  - 공백, 슬래시(/) 유무, 포트, 프로토콜(http/https)까지 모두 일치해야 함.
//package com.project.member.service;
//
//import java.io.IOException;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project.member.dto.KakaoUserInfoDto;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class KakaoApiServiceImpl implements KakaoApiService {
//
//	private final RestTemplate restTemplate = new RestTemplate();
//    private final ObjectMapper objectMapper;
//    //private final RestTemplate restTemplate;
//
//    // application.properties 에서 주입
//    @Value("${kakao.rest-api-key}")
//    private String restApiKey;
//    
//    @Value("${kakao.client-id}")
//    private String clientId;
//
//    @Value("${kakao.redirect-uri}") // ✅ 하드코딩 제거
//    private String redirectUri;
//
//    @Value("${kakao.token-uri}")
//    private String tokenUri;
//
//    @Value("${kakao.user-info-uri}")
//    private String userInfoUri;
//
//    /**
//     * 인가코드로 access_token 교환
//     *
//     * @param code 카카오 인가 서버가 리다이렉트로 전달한 인가코드
//     * @return access_token (문자열)
//     */
//    @Override
//    public String getAccessToken(String code) throws Exception {
//        try {
//            // ===== 요청 헤더 =====
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE); // JSON 응답 선호
//
//            // ===== 요청 파라미터 =====
//            // grant_type: authorization_code (고정)
//            // client_id : 카카오 REST API 키
//            // redirect_uri: 인가요청에 사용한 것과 반드시 동일
//            // code: 인가코드
//            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//            params.add("grant_type", "authorization_code");
//            params.add("client_id", clientId);
//            params.add("redirect_uri", redirectUri); // ✅ properties 주입 값 사용
//            params.add("code", code);
//
//            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
//
//            // ===== POST /oauth/token =====
//            ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);
//
//            String body = response.getBody();
//            if (body == null || body.isBlank()) {
//                throw new RuntimeException("카카오 토큰 응답이 비어 있습니다.");
//            }
//
//            JsonNode json = objectMapper.readTree(body);
//
//            // 예외 케이스 방어: error, error_description 존재 여부
//            if (json.hasNonNull("error")) {
//                String err = json.path("error").asText();
//                String desc = json.path("error_description").asText("");
//                throw new RuntimeException("카카오 토큰 요청 실패: " + err + (desc.isEmpty() ? "" : " - " + desc));
//            }
//
//            JsonNode tokenNode = json.get("access_token");
//            if (tokenNode == null || tokenNode.isNull()) {
//                throw new RuntimeException("access_token을 찾을 수 없습니다. 응답: " + body);
//            }
//
//            return tokenNode.asText();
//
//        } catch (HttpClientErrorException | HttpServerErrorException e) {
//            // 카카오 서버로부터의 4xx/5xx 응답
//            String details = e.getResponseBodyAsString();
//            System.out.println("🔴 카카오 토큰 교환 오류: " + e.getStatusCode() + " / " + details);
//            throw new RuntimeException("카카오 토큰 교환 실패: " + details, e);
//        } catch (IOException e) {
//            // JSON 파싱 오류
//            throw new RuntimeException("카카오 토큰 응답 파싱 실패", e);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("카카오 토큰 교환 중 알 수 없는 오류", e);
//        }
//    }
//
//    /**
//     * access_token 으로 사용자 정보 조회
//     *
//     * @param accessToken 카카오 액세스 토큰
//     * @return KakaoUserInfoDto (id, email, nickname, gender, birthday, birthyear, phoneNumber)
//     */
//    @Override
//    public KakaoUserInfoDto getUserInfo(String accessToken) throws Exception {
//        try {
//            // ===== 요청 헤더 =====
//            HttpHeaders headers = new HttpHeaders();
//            headers.setBearerAuth(accessToken); // Authorization: Bearer {token}
//            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//
//            HttpEntity<Void> entity = new HttpEntity<>(headers);
//
//            // Kakao 문서 기준: GET/POST 모두 허용. 여기서는 GET 사용.
//            ResponseEntity<String> response = restTemplate.exchange(
//                userInfoUri, HttpMethod.GET, entity, String.class
//            );
//
//            String body = response.getBody();
//            if (body == null || body.isBlank()) {
//                throw new RuntimeException("카카오 사용자 정보 응답이 비어 있습니다.");
//            }
//
//            JsonNode root = objectMapper.readTree(body);
//
//            // ===== 파싱 =====
//            KakaoUserInfoDto dto = new KakaoUserInfoDto();
//
//            // id (필수)
//            JsonNode idNode = root.get("id");
//            if (idNode == null || idNode.isNull()) {
//                throw new RuntimeException("카카오 사용자 정보에 id가 없습니다. 응답: " + body);
//            }
//            dto.setKakaoId(idNode.asText());
//
//            // kakao_account 내부
//            JsonNode account = root.get("kakao_account");
//            if (account != null && !account.isNull()) {
//                // email
//                if (account.hasNonNull("email")) {
//                    dto.setEmail(account.get("email").asText());
//                }
//
//                // gender ("male" | "female") - 동의 항목 / 권한 필요
//                if (account.hasNonNull("gender")) {
//                    dto.setGender(account.get("gender").asText());
//                }
//
//                // birthday ("MMDD"), birthyear ("YYYY")
//                if (account.hasNonNull("birthday")) {
//                    dto.setBirthday(account.get("birthday").asText());
//                }
//                if (account.hasNonNull("birthyear")) {
//                    dto.setBirthyear(account.get("birthyear").asText());
//                }
//
//                // profile.nickname
//                JsonNode profile = account.get("profile");
//                if (profile != null && profile.hasNonNull("nickname")) {
//                    dto.setNickname(profile.get("nickname").asText());
//                }
//
//                // phone_number ("+82 10-1234-5678") - 동의 항목 / 권한 필요
//                if (account.hasNonNull("phone_number")) {
//                    String raw = account.get("phone_number").asText();
//                    dto.setPhoneNumber(raw); // 원문 저장
//                }
//            }
//
//            return dto;
//
//        } catch (HttpClientErrorException | HttpServerErrorException e) {
//            String details = e.getResponseBodyAsString();
//            System.out.println("🔴 카카오 사용자 정보 오류: " + e.getStatusCode() + " / " + details);
//            throw new RuntimeException("카카오 사용자 정보 요청 실패: " + details, e);
//        } catch (IOException e) {
//            throw new RuntimeException("카카오 사용자 정보 응답 파싱 실패", e);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("카카오 사용자 정보 요청 중 알 수 없는 오류", e);
//        }
//    }
//
//    // ───────────────────────────────────────────────────────────────
//    // (선택) 전화번호 정규화 유틸 - 필요 시 컨트롤러/서비스단에서 사용
//    // "+82 10-1234-5678" → "01012345678"
//    // ───────────────────────────────────────────────────────────────
//    @SuppressWarnings("unused")
//    private String normalizeKakaoPhone(String phoneNumberFromKakao) {
//        if (phoneNumberFromKakao == null || phoneNumberFromKakao.isBlank()) return "";
//        // 국가코드 +82 제거 및 숫자만 남김
//        String digits = phoneNumberFromKakao.replaceAll("[^0-9]", "");
//        if (digits.startsWith("82")) {
//            digits = digits.substring(2); // "82" 제거
//        }
//        if (digits.startsWith("10")) {
//            digits = "0" + digits; // "10..." → "010..."
//        }
//        return digits;
//    }
//}
