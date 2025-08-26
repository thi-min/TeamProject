// src/main/java/com/project/member/dto/MemberPageResponseDto.java
package com.project.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 공통 페이지 응답 DTO (회원 전용)
 * - content: 실제 데이터 리스트
 * - page: 현재 페이지 번호
 * - size: 페이지 크기
 * - totalPages: 전체 페이지 수
 * - totalElements: 전체 데이터 수
 */
@Getter
@Setter
@AllArgsConstructor
public class MemberPageResponseDto<T> {
    private List<T> content;     // 데이터 목록
    private int page;            // 현재 페이지 번호
    private int size;            // 한 페이지 크기
    private int totalPages;      // 전체 페이지 수
    private long totalElements;  // 전체 데이터 수
}
