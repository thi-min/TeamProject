package com.project.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.admin.dto.AdminLoginRequestDto;
import com.project.admin.dto.AdminLoginResponseDto;
import com.project.admin.dto.AdminMemberDetailResponseDto;
import com.project.admin.dto.AdminPasswordUpdateRequestDto;
import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
import com.project.admin.service.AdminServiceImpl;
import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberState;
import com.project.member.repository.MemberRepository;
import com.project.member.service.MemberService;

@SpringBootTest
class AdminServiceImplTests{
	@Autowired
	private AdminServiceImpl adminService;
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private MemberRepository memberRepository;
	private MemberService memberService;
	private MemberEntity memberEntity;
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
	
//	//@Test
//	void 관리자회원목록조회_테스트() {
//		System.out.println("테스트 시작");
//		
//		List<AdminMemberListResponseDto> list = adminService.getMemberList(PageRequestDto pageRequestDto);
//		
//		System.out.println("목록 조회 완료");
//		
//		assertThat(list).isNotNull();
//		//최소 1명이 있는지 확인
//		assertThat(list.size()).isGreaterThanOrEqualTo(1);
//		
//		AdminMemberListResponseDto first = list.get(0);
//		
//		System.out.println("회원 목록 조회 결과:");
//		for (AdminMemberListResponseDto dto : list) {
//			System.out.println(dto);
//		}
//		
//		//아래 지정한 아이디가 있는지 확인
//		List<String> memberIds = list.stream().map(AdminMemberListResponseDto::getMemberId).toList();
//		assertThat(first.getMemberId()).isIn("test@test.com");
//
//		System.out.println(memberIds.toString());
//	}
	
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
    
//    //@Test
//    void 회원상태_정상변경() {
//    	Long memberNum = 3L;
//    	
//    	AdminMemberUpdateRequestDto result = adminService.adminMemberStateChange(3L, MemberState.ACTIVE);
//    	
//    	assertThat(result).isNotNull();
//    	assertThat(result.getMessage()).contains("정상");
//    	
//    	MemberEntity update = memberRepository.findByMemberNum(memberNum)
//    			.orElseThrow(() -> new RuntimeException("회원 조회 실패"));
//    	
//    	assertThat(update.getMemberState()).isEqualTo(MemberState.ACTIVE);
//    	
//    	System.out.println("회원상태 : " + update.getMemberState());
//    }
////    @Test
//    void 회원상태_휴먼변경() {
//    	Long memberNum = 1L;
//    	
//    	AdminMemberUpdateRequestDto result = adminService.adminMemberStateChange(1L, MemberState.REST);
//    	
//    	assertThat(result).isNotNull();
//    	assertThat(result.getMessage()).contains("휴먼");
//    	
//    	MemberEntity update = memberRepository.findByMemberNum(memberNum)
//    			.orElseThrow(() -> new RuntimeException("회원 조회 실패"));
//    	
//    	assertThat(update.getMemberState()).isEqualTo(MemberState.REST);
//    	
//    	System.out.println(result);
//    	System.out.println("회원상태 : " + update.getMemberState());
//    }
////    @Test
//    void 회원상태_탈퇴변경() {
//    	Long memberNum = 2L;
//    	
//    	AdminMemberUpdateRequestDto result = adminService.adminMemberStateChange(2L, MemberState.OUT);
//    	
//    	assertThat(result).isNotNull();
//    	assertThat(result.getMessage()).contains("탈퇴");
//    	
//    	MemberEntity update = memberRepository.findByMemberNum(memberNum)
//    			.orElseThrow(() -> new RuntimeException("회원 조회 실패"));
//    	
//    	assertThat(update.getMemberState()).isEqualTo(MemberState.OUT);
//    	
//    	System.out.println(result);
//    	System.out.println("회원상태 : " + update.getMemberState());
//    }
//    
//    //@Test
//    void 회원잠금_상태변경() {
//    	Long memberNum = 2L;
//    	
//    	AdminMemberUpdateRequestDto result = adminService.adminMemberStateChange(memberNum, true);
//    	
//    	assertThat(result).isNotNull();
//    	assertThat(result.getMessage()).contains("잠금상태");
//    	
//    	MemberEntity update = memberRepository.findByMemberNum(memberNum)
//    			.orElseThrow(() -> new RuntimeException("회원 조회 실패"));
//    	
//    	assertThat(update.getMemberLock()).isTrue();
//    	
//    	System.out.println(result);
//    	System.out.println("회원상태 : " + update.getMemberLock());
//    }


    // --------------------비밀번호 변경 -------------------
    //@Test
    void 비밀번호변경() {
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
    
    //관리자가 회원 삭제 로직(MemberState OUT으로 바뀌고 1분뒤 DB에서 삭제로직)
    @Test
    public void markTestUserForDeletion() {
        MemberEntity member = memberRepository.findByMemberId("test3@test.com")
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        member.setMemberState(MemberState.OUT); // ✅ 상태 OUT
        member.setOutDate(LocalDateTime.now().minusMinutes(1)); // ✅ 1~2분 전으로 설정

        memberRepository.save(member); // 변경 사항 저장
        System.out.println("✅ 회원 상태 변경 완료: test3@test.com");
    }
}