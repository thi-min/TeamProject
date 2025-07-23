package com.project.common.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

//페이지네이션 응답용 Dto
@Data
@Builder
public class PageResponseDto<T> {
	private List<T> content;	//현재 페이지에 해당하는 데이터 목록(게시글, 예약관리, 회원관리)
	private int currentPage;	//현재 페이지 번호
	private int totalPages;		//전체 페이지 수(총 데이터 수 / 페이지당 크기)
	private long totalElements;	//전체 데이터 개수(DB에 존재하는 전체 레코드 수)
	private boolean isFirst;	//현재 페이지가 첫 번째 페이지인지 여부
	private boolean isLast;		//현재 페이지가 마지막 페이지인지 여부
}
//예시 사용
//Page<MemberEntity> result = repository.findAll(pageable);
//PageResultDto<MemberResponseDto> dto = new PageResultDto<>(
//result.getCountent(),		//currentPage
//result.getNumber() + 1,	//기본제공 메서드
//result.getTotalPages(),	//totalPages
//result.getTotalElements(),//totalElements
//result.isFirst(),			//isFirst
//result.isLast()			//isLast
//);
