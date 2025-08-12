package com.project.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.project.common.dto.JusoSearchResponse;

import lombok.RequiredArgsConstructor;

//행안부 실시간 주소정보 '검색 API' 호출 구현
//GET/POST 모두 가능하나, 여기선 GET + resultType=json 사용
//키 노출 방지를 위해 반드시 서버에서 호출
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final RestTemplate restTemplate;

    @Value("${juso.api.base-url}")
    private String baseUrl;

    @Value("${juso.api.key}")
    private String confmKey;

    @Override
    public JusoSearchResponse search(String keyword, int page, int size) {
        // ✅ 요청 파라미터 구성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("confmKey", confmKey);
        params.add("currentPage", String.valueOf(page));
        params.add("countPerPage", String.valueOf(size));
        params.add("keyword", keyword);
        params.add("resultType", "json"); // JSON 응답

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/addrLinkApi.do")
                .queryParams(params)
                .build(true) // 인코딩 주의
                .toUriString();

        // ✅ 호출
        ResponseEntity<JusoSearchResponse> resp =
                restTemplate.exchange(url, HttpMethod.GET, null, JusoSearchResponse.class);

        JusoSearchResponse body = resp.getBody();
        if (body == null || body.getResults() == null || body.getResults().getCommon() == null) {
            throw new IllegalStateException("주소 API 응답 파싱 실패");
        }

        // ✅ 에러코드 체크 (0: 정상)
        String err = body.getResults().getCommon().getErrorCode();
        if (!"0".equals(err)) {
            throw new IllegalArgumentException(
                "주소 API 오류: " + err + " - " + body.getResults().getCommon().getErrorMessage());
        }

        return body;
    }
}
