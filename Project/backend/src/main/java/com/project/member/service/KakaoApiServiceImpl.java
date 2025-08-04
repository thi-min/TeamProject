package com.project.member.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.member.dto.KakaoUserInfoDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoApiServiceImpl {
	
	private final ObjectMapper objectMapper;
	
	//application.properties에서 주입받는 설정 값들
    @Value("${kakao.client-id}")
    private String clientId;
    @Value("${kakao.redirect-uri}")
    private String redirectUri;
    @Value("${kakao.token-uri}")
    private String tokenUri;
    @Value("${kakao.user-info-uri}")
    private String userInfoUri;
    
    //카카오로부터 전달받은 인가코드를 통해 access_token을 요청하는 메서드
    //param : code 카카오 로그인 후 전달받은 인가 코드(REST API)
    //return : access token 문자열
    //throws : Exception JSON 파싱 실패시 예외 발생
    public String getAccessToken(String code) throws Exception{
    	RestTemplate restTemplate = new RestTemplate();
    	
    	//요청 헤더 설정
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    	
    	//요청 파라미터 설정
    	Map<String, String> params = new HashMap<>();
    	params.put("grant_type", "authorization_code");		//고정값
    	params.put("client_id", clientId);					//카카오 REST API 키값
    	params.put("redirect_uri", redirectUri);			//카카오에 등록된 리다이렉트 URI
    	params.put("code", code);							//인가코드
    	
    	//요청 생성
    	HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);
    	
    	//POST 요청 보내고 응답 받기
    	ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);
    	
    	//응답 JSON 파싱
    	JsonNode json = objectMapper.readTree(response.getBody());
    	
    	//access_token 추출 후 반환
    	return json.get("access_token").asText();
    }
    
    //access_token을 사용해 카카오 사용자 정보를 조회하는 메서드
    //param : accessToken 카카오로부터 받은 access_token
    //return : 사용자 정보(kakaoUserInfoDto) 객체
    //throws : Exception JSON 파싱 실패시 예외 발생
    public KakaoUserInfoDto getUserInfo(String accessToken) throws Exception{
    	RestTemplate restTemplate = new RestTemplate();
    	
    	//요청 헤더 설정
    	HttpHeaders headers = new HttpHeaders();
    	//Authorization: Bearer {access_token}
    	headers.setBearerAuth(accessToken);		
    	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    	
    	//요청생성
    	HttpEntity<?> entity = new HttpEntity<>(headers);
    	
    	//사용자 정보 요청
    	ResponseEntity<String> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, String.class);
    	
    	//응답 JSON 파싱
    	JsonNode json = objectMapper.readTree(response.getBody());
    	
    	//사용자 정보 객체 생성 및 값 설정
    	KakaoUserInfoDto userInfo = new KakaoUserInfoDto();
    	userInfo.setKakaoId(json.get("kakaoId").asText());	//카카오 ID
    	
    	//kakao_account 내부 정보 파싱(카카오계정(이메일), 이름, 성별, 생일, 출생연도, 카카오계정(전화번호))
    	// ✅ kakao_account 노드에서 사용자 정보 파싱
    	JsonNode account = json.get("kakao_account");
    	
    	if (account != null) {
    	    if (account.has("email")) userInfo.setEmail(account.get("email").asText()); // 카카오 이메일
    	    if (account.has("gender")) userInfo.setGender(account.get("gender").asText()); // 성별
    	    if (account.has("birthday")) userInfo.setBirthday(account.get("birthday").asText()); // 생일 MMDD
    	    if (account.has("birthyear")) userInfo.setBirthyear(account.get("birthyear").asText()); // 출생연도 YYYY
    	    //전화번호 +82 10 으로 들어와서 이부분 수정필요
    	    if (account.has("phone_number")) userInfo.setPhoneNumber(account.get("phone_number").asText()); // +82 전화번호

    	    // 닉네임은 profile 하위에 있음
    	    JsonNode profile = account.get("profile");
    	    if (profile != null && profile.has("nickname")) {
    	        userInfo.setNickname(profile.get("nickname").asText()); // 사용자 닉네임
    	    }
    	}

    	return userInfo;	//kakaoUserInfoDto
    }
}
