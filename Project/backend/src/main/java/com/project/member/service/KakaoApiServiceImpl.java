package com.project.member.service;

import com.project.member.dto.KakaoUserInfoDto;
import com.project.member.service.KakaoApiService;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoApiServiceImpl implements KakaoApiService {

	 // RestTemplate 빈을 따로 두지 않았다면 간단히 내부에서 생성
    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ 당신의 application.properties 키 이름과 정확히 매칭
    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUrl;

    @Value("${kakao.user-info-uri}")
    private String userInfoUrl;


    @Override
    public String getAccessToken(String code) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "authorization_code");
            form.add("client_id", clientId);      // ← null 되면 반드시 실패
            form.add("redirect_uri", redirectUri); // ← 프론트/콘솔과 한 글자도 다르면 실패
            form.add("code", code);

            HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);
            ResponseEntity<Map> res = restTemplate.postForEntity(tokenUrl, req, Map.class);

            if (res.getStatusCode() != HttpStatus.OK || res.getBody() == null) {
                throw new RuntimeException("Kakao token http error: " + res.getStatusCode());
            }

            Object at = res.getBody().get("access_token");
            if (at == null) {
                log.error("[KAKAO TOKEN ERROR] body={}", res.getBody()); // ← 에러바디 직접 확인
                throw new RuntimeException("Kakao token error: " + res.getBody());
            }
            return String.valueOf(at);
        } catch (HttpStatusCodeException e) {
            log.error("[KAKAO TOKEN EXCEPTION] status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new Exception("Kakao token request failed: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("[KAKAO TOKEN EXCEPTION] {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public KakaoUserInfoDto getUserInfo(String accessToken) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
            HttpEntity<Void> req = new HttpEntity<>(headers);

            ResponseEntity<Map> res = restTemplate.exchange(userInfoUrl, HttpMethod.GET, req, Map.class);
            if (res.getStatusCode() != HttpStatus.OK || res.getBody() == null) {
                throw new RuntimeException("Kakao userinfo http error: " + res.getStatusCode());
            }

            Map body = res.getBody();

            // id (필수)
            String kakaoId = body.get("id") == null ? null : String.valueOf(body.get("id"));

            // kakao_account
            Map acc = (Map) body.get("kakao_account");
            String email       = acc == null ? null : (String) acc.get("email");
            String gender      = acc == null ? null : (String) acc.get("gender");       // "male"/"female"
            String birthday    = acc == null ? null : (String) acc.get("birthday");     // "MMDD"
            String birthyear   = acc == null ? null : (String) acc.get("birthyear");    // "YYYY"
            String phoneNumber = acc == null ? null : (String) acc.get("phone_number"); // "+82 10-..."

            // nickname: profile.nickname → 없으면 properties.nickname
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

            KakaoUserInfoDto dto = new KakaoUserInfoDto();
            dto.setKakaoId(kakaoId);
            dto.setEmail(email);
            dto.setNickname(nickname);
            dto.setGender(gender);
            dto.setBirthday(birthday);
            dto.setBirthyear(birthyear);
            dto.setPhoneNumber(phoneNumber);
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
