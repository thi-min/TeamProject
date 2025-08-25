// src/main/java/com/project/common/dto/MemberPageRequestDto.java
package com.project.member.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 공용 페이지 요청 DTO
 * - page: 0부터 시작
 * - size: 한 페이지 크기
 * - keyword: 검색어(선택)
 * - memberNum: 회원 단건 조회(선택)
 */
@Getter
@Setter
@AllArgsConstructor
public class MemberPageRequestDto {
    private int page = 0;      // 기본값 0
    private int size = 10;     // 기본값 10
    private String keyword;    // 검색어
    private Long memberNum;    // 회원번호 검색 (예시)

    public Pageable toPageable(Sort sort) {
        return PageRequest.of(page, size, sort);
    }
}
