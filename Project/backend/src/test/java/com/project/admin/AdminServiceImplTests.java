package com.project.admin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.admin.dto.AdminLoginRequestDto;
import com.project.admin.dto.AdminLoginResponseDto;
import com.project.admin.dto.AdminMemberListResponseDto;
import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
import com.project.admin.service.AdminServiceImpl;

@SpringBootTest
class AdminServiceImplTests{
	@Autowired
	private AdminServiceImpl adminService;
	
	@Autowired
	private AdminRepository adminRepository;
	
	private Long testAdminNum;
	
	//관리자 조회
	@BeforeEach
	void setUp() {
		Optional<AdminEntity> admin = adminRepository.findFirstByAdminId("admin");
		if(admin.isEmpty()) {
			throw new RuntimeException("관리자 정보가 존재하지 않습니다.");
		}
		testAdminNum = admin.get().getAdminNum();
	}
	
	//@Test
	void 관리자로그인_테스트() {
		AdminLoginRequestDto loginDto = new AdminLoginRequestDto("admin","1234");
		AdminLoginResponseDto result = adminService.login(loginDto);
        
        assertThat(result).isNotNull();
		assertThat(result.getAdminId()).isEqualTo("admin");
		assertThat(result.getMessage()).isEqualTo("관리자 로그인 성공");
		
		System.out.println(loginDto.toString());
		System.out.println("로그인 결과: " + result); // assert 전에 출력

	}
	
	//@Test
	void 관리자회원목록조회_테스트() {
		System.out.println("테스트 시작");
		
		List<AdminMemberListResponseDto> list = adminService.adminMemberList();
		
		System.out.println("목록 조회 완료");
		
		assertThat(list).isNotNull();
		//최소 1명이 있는지 확인
		assertThat(list.size()).isGreaterThanOrEqualTo(1);
		
		AdminMemberListResponseDto first = list.get(0);
		
		System.out.println("회원 목록 조회 결과:");
		for (AdminMemberListResponseDto dto : list) {
			System.out.println(dto);
		}
		
		//아래 지정한 아이디가 있는지 확인
		List<String> memberIds = list.stream().map(AdminMemberListResponseDto::getMemberId).toList();
		assertThat(first.getMemberId()).isIn("test@test.com");

		System.out.println(memberIds.toString());
	}
	
	@Test
	//@Test
	void 회원목록_상세보기_테스트() {
		System.out.println("테스트 시작");
		
		List<AdminMemberDetailResponseDto> list = adminService.adminMemberDetailView();
		
		System.out.println("목록 조회 완료");
		
		assertThat(list).isNotNull();
		//최소 1명이 있는지 확인
		assertThat(list.size()).isGreaterThanOrEqualTo(1);
		
		AdminMemberListResponseDto first = list.get(0);
		
		System.out.println("회원 목록 조회 결과:");
		for (AdminMemberListResponseDto dto : list) {
			System.out.println(dto);
		}
		
		//아래 지정한 아이디가 있는지 확인
		List<String> memberIds = list.stream().map(AdminMemberListResponseDto::getMemberId).toList();
		assertThat(first.getMemberId()).isIn("test@test.com");

		System.out.println(memberIds.toString());
	}

	@Test
	//관리자가 회원 정보 상태를 수정 _ 휴먼/정상/탈퇴 and 
	void 회원정보수정수정(){
		System.out.println("test start");

		d
	}
}

