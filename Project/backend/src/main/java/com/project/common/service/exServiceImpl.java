package com.project.common.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.admin.dto.AdminMemberListResponseDto;
import com.project.admin.repository.AdminRepository;
import com.project.common.dto.PageRequestDto;
import com.project.common.dto.PageResponseDto;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

//회원목록 조회 기준
@Service //tjqltmrPcmd(spring bean)으로 등록
@Transactional
@RequiredArgsConstructor //final로 선언된 memberRepository를 자동으로 생성자 주입 시켜줌
public class exServiceImpl {

	private final AdminRepository adminRepository;
	private final MemberRepository memberRepository;
	
	//페이지네이션
	public PageResponseDto<AdminMemberListResponseDto> getMemberList(PageRequestDto pageRequestDto){
		//page, size, sort 정보가 들어감
		Pageable pageable = pageRequestDto.toPageable();
		Page<MemberEntity> result;
		
		//검색 키워드가 있을 경우 > 이름에 키워드가 포함된 회원만 조회
		if(pageRequestDto.getKeyword() != null && !pageRequestDto.getKeyword().isBlank()) {
			result = memberRepository.findByMemberNameContaining(pageRequestDto.getKeyword(), pageable);
		}else {
			//검색키워드가 없으면 전체 목록 조회
			result = memberRepository.findAll(pageable);
		}
		
		//Entity > Dto로 변환(프론트에 필요한 데이터 형태로 매핑시킴)
		List<AdminMemberListResponseDto> dtoList = result.getContent().stream()
				.map(member -> AdminMemberListResponseDto.builder()
						.memberNum(member.getMemberNum())							//회원 고유번호
						.memberId(member.getMemberId())								//회원 아이디
						.memberName(member.getMemberName())							//회원 이름
						.memberDay(member.getMemberDay().toString())				//가입일(LocalDate -> 문자열 변환)
						.memberState(member.getMemberState().name())				//회원상태 (enum -> 문자열 변환)
						.memberLock(Boolean.TRUE.equals(member.getMemberLock()))	//계정 잠금여부
						.build()
				)
				.toList();
		
		//페이지 결과를 PageResponseDto 형태로 래핑해서 리턴
		return PageResponseDto.<AdminMemberListResponseDto>builder()
				.content(dtoList)								//현재 페이지에 해당하는 데이터 목록
				.currentPage(result.getNumber() + 1)			//현재 페이지 번호
				.totalPages(result.getTotalPages())				//전체 페이지 수
				.totalElements(result.getTotalElements())		//전체 데이터 수(회원, 예약, 게시판 등등)
				.isFirst(result.isFirst())						//첫 페이지 여부
				.isLast(result.isLast())						//마지막 페이지 여부
				.build();
	}
}
