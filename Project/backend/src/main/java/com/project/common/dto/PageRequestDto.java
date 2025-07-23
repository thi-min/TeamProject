package com.project.common.dto;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//페이지네이션 요청 Dto
@Data
@Builder
@Getter
@Setter
public class PageRequestDto{
	private int page = 1;	//현재페이지(기본값 1)
	private int size = 10;	//페이지당 항목 수(기본값 10)
	private String keyword;	//검색 키워드(추후 확인)
	
	//sortBy 정렬데이터 기준이라 요것만 바꾸면 됩니다.
	private String sortBy = "id";	//기본정렬 기준 필드
	private String direction = "DESC";	//ASC or DESC
	
	//JPA Pageable 변환 메서드
	public Pageable toPageable(){
		//정렬 조건 설정
		//direction(문자열)이 "ASC"이면 오름차순, 아니면 내림차순
		//equalsIgnoreCase - 대소문자 관계없이 비교함
		Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		
		//PageRequest.of(페이지번호, 페이지크기, 정렬조건) 형태
		//JPA는 페이지번호 0번부터여서 page -1 필수
		return PageRequest.of(page - 1, size, sort);
	}
}