package com.project.member.service;

import com.project.member.dto.KakaoUserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Kakao OAuth2 연동 서비스 구현
 *
 * 핵심 포인트
 * - KOE320(invalid_grant) 회피:
 *   1) 토큰 교환 시 redirect_uri가 "인가 코드 발급에 사용된 것"과 '완전히' 동일해야 함
 *      (ex. http://127.0.0.1:3000/oauth/kakao/callback)
 *   2) React StrictMode로 콜백이 2번 호출되면 code가 재사용되어 실패 → 프론트에서 중복호출 가드 필요
 * - Kakao 앱에 Client Secret이 "사용"으로 되어 있으면 반드시 함께 전송해야 함.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoApiServiceImpl implements KakaoApiService {

    // ✅ RestTemplate를 간단히 내부 생성(Bean 주입 형태도 무방)
    private final RestTemplate restTemplate = new RestTemplate();

    // =========================================
    // 🔧 application.properties / yml 설정 값
    //    (네가 기존에 쓰던 키 이름 그대로 유지)
    // =========================================
    @Value("${kakao.client-id}")                 // ex) REST API 키
    private String clientId;

    // ⚠ redirect-uri는 "카카오 개발자 콘솔에 등록된 것"과 '완전히 동일'해야 한다.
    //    (localhost vs 127.0.0.1, http vs https, 포트/경로 한 글자라도 다르면 실패)
    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    // 카카오 토큰 엔드포인트 (기본값을 안전하게 제공)
    @Value("${kakao.token-uri:https://kauth.kakao.com/oauth/token}")
    private String tokenUrl;

    // 카카오 유저정보 엔드포인트 (기본값 제공)
    @Value("${kakao.user-info-uri:https://kapi.kakao.com/v2/user/me}")
    private String userInfoUrl;

    // ✅ (선택) Kakao 앱에서 Client Secret을 "사용"으로 설정했다면 반드시 필요
    //    사용하지 않을 경우 properties에 키를 비워두면 됨(기본 빈 문자열).
    @Value("${kakao.client-secret:}")
    private String clientSecret;

    // ============================
    // 🔑 인가코드 → Access Token
    // ============================
    @Override
    public String getAccessToken(String code) throws Exception {
        try {
            // 1) 요청 헤더 (x-www-form-urlencoded)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

            // 2) 요청 바디
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "authorization_code");
            form.add("client_id", clientId);              // ← null/빈값이면 반드시 실패
            form.add("redirect_uri", redirectUri);        // ← 프론트/콘솔과 '한 글자도' 다르면 실패(KOE320)
            form.add("code", code);

            // ⚠ Kakao 앱에서 Client Secret 사용 중이라면 반드시 포함
            if (clientSecret != null && !clientSecret.isBlank()) {
                form.add("client_secret", clientSecret);
            }

            HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);

            // 3) 토큰 요청
            ResponseEntity<Map> res = restTemplate.postForEntity(tokenUrl, req, Map.class);

            if (res.getStatusCode() != HttpStatus.OK || res.getBody() == null) {
                throw new RuntimeException("Kakao token http error: " + res.getStatusCode());
            }

            // 4) access_token 추출
            Object at = res.getBody().get("access_token");
            if (at == null) {
                // 카카오가 OK를 주더라도 바디에 에러 구조일 수 있으므로 바디 전체 로깅
                log.error("[KAKAO TOKEN ERROR] body={}", res.getBody());
                throw new RuntimeException("Kakao token error: " + res.getBody());
            }

            // 디버깅용 참고 로그 (운영에서는 레벨 조정 권장)
            log.debug("[KAKAO TOKEN OK] redirectUri={}, hasSecret={}, scope={}",
                    redirectUri, (clientSecret != null && !clientSecret.isBlank()),
                    res.getBody().get("scope"));

            return String.valueOf(at);

        } catch (HttpStatusCodeException e) {
            // 카카오 에러 바디 그대로 노출 → 프론트/로그에서 원인 파악 쉬움 (ex. KOE320)
            log.error("[KAKAO TOKEN EXCEPTION] status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new Exception("Kakao token request failed: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("[KAKAO TOKEN EXCEPTION] {}", e.getMessage(), e);
            throw e;
        }
    }

    // ============================
    // 🧑 Access Token → 사용자 정보
    // ============================
    @Override
    public KakaoUserInfoDto getUserInfo(String accessToken) throws Exception {
        try {
            // 1) 요청 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);                  // Authorization: Bearer {token}
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
            HttpEntity<Void> req = new HttpEntity<>(headers);

            // 2) 사용자 정보 요청
            ResponseEntity<Map> res = restTemplate.exchange(userInfoUrl, HttpMethod.GET, req, Map.class);
            if (res.getStatusCode() != HttpStatus.OK || res.getBody() == null) {
                throw new RuntimeException("Kakao userinfo http error: " + res.getStatusCode());
            }

            Map body = res.getBody();

            // ========== 필드 파싱 ==========
            // id (필수)
            String kakaoId = body.get("id") == null ? null : String.valueOf(body.get("id"));

            // kakao_account (옵션 필드 존재 가능)
            Map acc = (Map) body.get("kakao_account");
            String email       = acc == null ? null : (String) acc.get("email");
            String gender      = acc == null ? null : (String) acc.get("gender");        // "male" / "female"
            String birthday    = acc == null ? null : (String) acc.get("birthday");      // "MMDD"
            String birthyear   = acc == null ? null : (String) acc.get("birthyear");     // "YYYY"
            String phoneNumber = acc == null ? null : (String) acc.get("phone_number");  // "+82 10-...."
            String name        = acc == null ? null : (String) acc.get("name");          // scope 동의 필요

            // nickname은 properties 또는 kakao_account.profile에서 가져올 수 있음
            String nickname = null;
            Map profile = acc == null ? null : (Map) acc.get("profile");
            if (profile != null && profile.get("nickname") != null) {
                nickname = String.valueOf(profile.get("nickname"));
            } else {
                Map props = (Map) body.get("properties");
                if (props != null && props.get("nickname") != null) {
                    nickname = String.valueOf(props.get("nickname"));
                }
            }

            // 이름(name)이 비어있으면 nickname으로 대체(프리필 UX 보완)
            if (name == null || name.isBlank()) {
                name = nickname;
            }

            // 3) DTO 구성
            KakaoUserInfoDto dto = new KakaoUserInfoDto();
            dto.setKakaoId(kakaoId);     // ✔ 카카오 고유 ID → 우리 프로젝트에선 memberId로 사용
            dto.setEmail(email);
            dto.setName(name);
            dto.setGender(gender);
            dto.setBirthday(birthday);
            dto.setBirthyear(birthyear);
            dto.setPhoneNumber(phoneNumber);

            // 디버깅용 참고 로그 (운영에서는 레벨 조정 권장)
            log.debug("[KAKAO USERINFO OK] id={}, email={}, name={}", kakaoId, email, name);

            return dto;

        } catch (HttpStatusCodeException e) {
            log.error("[KAKAO USERINFO EXCEPTION] status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new Exception("Kakao userinfo failed: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("[KAKAO USERINFO EXCEPTION] {}", e.getMessage(), e);
            throw e;
        }
    }
}
