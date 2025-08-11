package com.project.common.dto;

import java.util.List;

import lombok.Data;

@Data
public class JusoSearchResponse {
    private Results results;

    @Data
    public static class Results {
        private Common common;
        private List<Juso> juso;
    }

    @Data
    public static class Common {
        private String errorCode;     // "0" 이면 정상
        private String errorMessage;
        private String totalCount;
        private int currentPage;
        private int countPerPage;
    }

    @Data
    public static class Juso {
        private String roadAddr;       // 전체 도로명주소
        private String roadAddrPart1;  // 참고항목 제외
        private String roadAddrPart2;  // 참고항목
        private String jibunAddr;      // 지번주소
        private String zipNo;          // 우편번호
        private String admCd;          // 행정구역코드
        private String rnMgtSn;        // 도로명코드
        private String bdMgtSn;        // 건물관리번호
        private String bdNm;           // 건물명(있을 때)
        private String detBdNmList;    // 상세건물명 리스트(있을 때)
        //private String bdKdcd;         // 공동주택 여부(1/0)
        private String siNm;           // 시도
        private String sggNm;          // 시군구
        private String emdNm;          // 읍면동
        // 필요 시 필드 추가 가능 (문서의 출력 항목 참조)
    }
}
