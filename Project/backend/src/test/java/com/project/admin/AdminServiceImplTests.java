package com.project.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.admin.dto.AdminForcedDeleteDto;
import com.project.admin.dto.AdminLoginRequestDto;
import com.project.admin.dto.AdminLoginResponseDto;
import com.project.admin.dto.AdminMemberDetailResponseDto;
import com.project.admin.dto.AdminMemberListResponseDto;
import com.project.admin.dto.AdminPasswordUpdateRequestDto;
import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
import com.project.admin.service.AdminServiceImpl;
import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberState;
import com.project.member.repository.MemberRepository;

@SpringBootTest
class AdminServiceImplTests{
	@Autowired
	private AdminServiceImpl adminService;
	
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private MemberRepository memberRepository;
	
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
	
	//@Test
    void 관리자회원상세조회_테스트() {
        AdminMemberDetailResponseDto detail = adminService.adminMemberDetailView(3L);

        System.out.println("회원 상세정보:");
        System.out.println(detail);

        assertThat(detail).isNotNull();
        assertThat(detail.getMemberNum()).isEqualTo(3L);
        assertThat(detail.getMemberId()).isNotBlank(); // 아이디(이메일) 존재 확인
        assertThat(detail.getMemberName()).isNotBlank(); // 이름 존재 확인
    }
    
    //@Test
    void 회원탈퇴시키기() {
    	Long testMemberNum = 3L;
        //회원 강제 탈퇴
    	AdminForcedDeleteDto result = adminService.adminMemberOut(testMemberNum);
    	System.out.println("삭제할 회원 조회 완료");
    	
    	//결과검증
    	assertThat(result).isNotNull();
    	assertThat(result.getMemberNum()).isEqualTo(testMemberNum);
    	assertThat(result.getMessage()).isEqualTo("회원 탈퇴 완료");
    	
    	//회원상태가 OUT으로 바뀌었는지 검사
    	MemberEntity updated = memberRepository.findByMemberNum(testMemberNum)
    			.orElseThrow(() -> new RuntimeException("회원 조회 실패"));
    	assertThat(updated.getMemberState()).isEqualTo(MemberState.OUT);
    	
    	//출력
    	System.out.println(result);
    	System.out.println("회원 상태 : " + updated.getMemberState());
    }
    
    // --------------------비밀번호 변경 -------------------
    //@Test
    @DisplayName("1. 비밀번호 변경 성공")
    void updatePassword() {
    	String adminId = "admin";
    	AdminEntity admin = AdminEntity.builder()
    			.adminId(adminId)
    			.adminPw("1234")
    			.build();
    	
    	AdminPasswordUpdateRequestDto dto = new AdminPasswordUpdateRequestDto();
    	dto.setCurrentPassword("1234"); //현재 비밀번호
    	dto.setNewPassword("dks123"); //새로운 비밀번호
    	dto.setNewPasswordCheck("dks123"); //새로운 비밀번호 일치 확인
    	
    	//비밀번호 변경
    	adminService.updatePassword(adminId, dto);
    	//db조회 변경확인
    	AdminEntity update = adminRepository.findFirstByAdminId(adminId)
    			.orElseThrow(() -> new RuntimeException("관리자 없음"));
    	
    	assertEquals("dks123", update.getAdminPw());
    	
    	System.out.println("현재 비밀번호" + update.getAdminPw());
    	
    }
}