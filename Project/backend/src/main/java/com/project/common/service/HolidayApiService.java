package com.project.common.service;

import com.project.common.dto.HolidayDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayApiService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${holiday.api.key}")
    private String serviceKey;
    
    //공휴일 정보 조회 기능 기본요청 url
    private static final String BASE_URL = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";

    public List<HolidayDto> getHolidays(int year) {
        List<HolidayDto> holidays = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            holidays.addAll(fetch(year, month));
        }
        return holidays;
    }

    private List<HolidayDto> fetch(int year, int month) {
        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("solYear", year)
                .queryParam("solMonth", String.format("%02d", month))
                .queryParam("ServiceKey", serviceKey)
                .queryParam("_type", "json")
                .queryParam("numOfRows", 30)
                .build(true)
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        if (!response.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON)) {
            throw new RuntimeException("API 응답이 JSON 형식이 아닙니다.");
        }

        if (response.getStatusCode().is2xxSuccessful()) {
            return parse(response.getBody());
        } else {
            throw new RuntimeException("공휴일 API 호출 실패: " + response.getStatusCode());
        }
    }
    
    

    private List<HolidayDto> parse(String json) {
        List<HolidayDto> result = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode itemsNode = root.path("response").path("body").path("items");
            JsonNode itemNode = itemsNode.path("item");

            if (itemNode.isArray()) {
                for (JsonNode item : itemNode) {
                    result.add(parseItem(item));
                }
            } else if (itemNode.isObject()) {
                result.add(parseItem(itemNode));
            }
        } catch (Exception e) {
            log.error("공휴일 JSON 파싱 오류: {}", json);
            throw new RuntimeException("공휴일 JSON 파싱 오류", e);
        }
        return result;
    }

    private HolidayDto parseItem(JsonNode item) {
        int locdate = item.path("locdate").asInt(); // 예: 20250815
        String dateName = item.path("dateName").asText(); // 예: 광복절
        String isHoliday = item.path("isHoliday").asText(); // "Y" or "N"
        
        System.out.println("공휴일 원본 데이터 → locdate=" + locdate 
                + ", dateName=" + dateName 
                + ", isHoliday=" + isHoliday);
        
        LocalDate date = LocalDate.parse(String.valueOf(locdate), DateTimeFormatter.BASIC_ISO_DATE);
        return HolidayDto.builder()
                .date(date)
                .name(dateName)
                .isHoliday(isHoliday)
                .build();
    }
}