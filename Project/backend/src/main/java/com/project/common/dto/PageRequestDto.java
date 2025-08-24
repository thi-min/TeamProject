package com.project.common.dto;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDto {
    private int page = 1;     // 프론트는 1부터
    private int size = 10;    // 기본 크기
    private String keyword;   // 이름 검색

    public Pageable toPageable() {
        int pageIndex = Math.max(0, page - 1); // 0 기반으로 보정
        // ✅ 정렬은 당분간 제거 (오타로 500 나는 케이스 많음)
        return PageRequest.of(pageIndex, size);
    }
}
