package com.project.member.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.common.jwt.JwtTokenProvider;
import com.project.member.dto.KakaoUserInfoDto;
import com.project.member.dto.MemberLoginResponseDto;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoApiServiceImpl2 implements KakaoApiService{
	
	private final ObjectMapper objectMapper;
	private final RestTemplate restTemplate;
	
	//application.properties에서 주입받는 설정 값들
    @Value("${kakao.client-id}")
    private String clientId;
    //@Value("${kakao.redirect-uri}")
    //private String redirectUri;
    @Value("${kakao.token-uri}")
    private String tokenUri;
    @Value("${kakao.user-info-uri}")
    private String userInfoUri;
    
    //카카오로부터 전달받은 인가코드를 통해 access_token을 요청하는 메서드
    //param : code 카카오 로그인 후 전달받은 인가 코드(REST API)
    //return : access token 문자열
    //throws : Exception JSON 파싱 실패시 예외 발생
    public String getAccessToken(String code) throws Exception{

    	try {
    		System.out.println("🧪 getAccessToken 파라미터 확인");
    		System.out.println("🧪 code: " + code);

    		
	    	//요청 헤더 설정
	    	HttpHeaders headers = new HttpHeaders();
	    	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    	
	    	//요청 파라미터 설정
	    	MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	    	params.add("grant_type", "authorization_code");		//고정값
	    	params.add("client_id", clientId);					//카카오 REST API 키값
	    	params.add("redirect_uri", "/oauth/kakao/callback");
	    	//params.add("redirect_uri", redirectUri);			//카카오에 등록된 리다이렉트 URI
	    	params.add("code", code);							//인가코드
	    	
	    	//요청 생성
	    	HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
	    	
	    	//POST 요청 보내고 응답 받기
	    	ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);
	    	System.out.println("응답: " + response);
	    	System.out.println("본문: " + response.getBody());
	    	
	    	//응답 본문이 비어있는경우 예외처리
	    	String responseBody = response.getBody();
	    	if(responseBody == null) {
	    		throw new RuntimeException("카카오로부터 받은 응답이 비어 있습니다.");
	    	}
	    	
	    	//응답 JSON 파싱
	    	JsonNode json = objectMapper.readTree(response.getBody());
	    	
	    	//access_token 추출 후 반환
	    	return json.get("access_token").asText();
	    	
    	}catch(HttpClientErrorException  | HttpServerErrorException e) {
    		System.out.println("🔴 카카오 API 오류 코드: " + e.getStatusCode());
    	    System.out.println("🔴 카카오 API 오류 내용: " + e.getResponseBodyAsString());
    		throw new RuntimeException("카카오 API 요청 실패: " + e.getResponseBodyAsString(), e);
    	}catch(IOException e) {
    		throw new RuntimeException("카카오 응답파싱 중 오류 발생", e);
    	}catch(Exception e) {
    		e.printStackTrace(); // ✅ 콘솔에 실제 원인 출력
    		throw new RuntimeException("알 수 없는 오류 발생", e);
    	}
    }
    
    //access_token을 사용해 카카오 사용자 정보를 조회하는 메서드
    //param : accessToken 카카오로부터 받은 access_token
    //return : 사용자 정보(kakaoUserInfoDto) 객체
    //throws : Exception JSON 파싱 실패시 예외 발생
    public KakaoUserInfoDto getUserInfo(String accessToken) throws Exception{
    	System.out.println("🟢 사용자 정보 조회용 AccessToken: " + accessToken); // 추가
    	
    	//요청 헤더 설정
    	HttpHeaders headers = new HttpHeaders();
    	//Authorization: Bearer {access_token}
    	headers.setBearerAuth(accessToken);		
    	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    	
    	//요청생성
    	HttpEntity<?> entity = new HttpEntity<>(headers);
    	
    	//사용자 정보 요청
    	ResponseEntity<String> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, entity, String.class);
    	System.out.println("🟢 사용자 정보 응답: " + response);
        System.out.println("🟢 사용자 정보 바디: " + response.getBody());
    	
    	//응답 JSON 파싱
    	JsonNode json = objectMapper.readTree(response.getBody());
    	
    	//사용자 정보 객체 생성 및 값 설정
    	KakaoUserInfoDto userInfo = new KakaoUserInfoDto();
    	userInfo.setKakaoId(json.get("id").asText());	//카카오 ID
    	
    	//kakao_account 내부 정보 파싱(카카오계정(이메일), 이름, 성별, 생일, 출생연도, 카카오계정(전화번호))
    	// ✅ kakao_account 노드에서 사용자 정보 파싱
    	JsonNode account = json.get("kakao_account");
    	
    	if (account != null) {
    	    if (account.has("email")) userInfo.setEmail(account.get("email").asText()); // 카카오 이메일
    	    if (account.has("gender")) userInfo.setGender(account.get("gender").asText()); // 성별
    	    if (account.has("birthday")) userInfo.setBirthday(account.get("birthday").asText()); // 생일 MMDD
    	    if (account.has("birthyear")) userInfo.setBirthyear(account.get("birthyear").asText()); // 출생연도 YYYY
            
    	    // 닉네임은 profile 하위에 있음
    	    JsonNode profile = account.get("profile");
    	    if (profile != null && profile.has("nickname")) {
    	        userInfo.setNickname(profile.get("nickname").asText()); // 사용자 닉네임
    	    }
    	}

    	return userInfo;	//kakaoUserInfoDto
    }
    
    

}
