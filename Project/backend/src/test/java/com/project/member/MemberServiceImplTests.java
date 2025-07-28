package com.project.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.member.dto.MemberDeleteDto;
import com.project.member.dto.MemberLoginRequestDto;
import com.project.member.dto.MemberLoginResponseDto;
import com.project.member.dto.MemberMyPageResponseDto;
import com.project.member.dto.MemberMyPageUpdateRequestDto;
import com.project.member.dto.MemberPasswordUpdateRequestDto;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.member.service.MemberServiceImpl;

@SpringBootTest
class MemberServiceImplTests {

    @Autowired
    private MemberServiceImpl memberService;

    @Autowired
    private MemberRepository memberRepository;

    private Long testMemberNum;

    //@BeforeEach
//    void setUp() {
//        Optional<MemberEntity> member = memberRepository.findByMemberId("test@test.com");
//        if (member.isEmpty()) {
//            throw new RuntimeException("사전 테스트 회원이 존재하지 않습니다.");
//        }
//        testMemberNum = member.get().getMemberNum();
//    }



    //@Test
    void 마이페이지_조회_테스트() {
        MemberMyPageResponseDto dto = memberService.myPage(1L);

        assertThat(dto).isNotNull();
        assertThat(dto.getMemberId()).isEqualTo("test@test.com");
        System.out.println(dto.toString());
    }
    
   //@Test
   void 마이페이지_수정_테스트() {
	   Long memberNum = 4L; // DB에 존재하는 회원 번호 사용
       MemberMyPageUpdateRequestDto updateDto = new MemberMyPageUpdateRequestDto();
       updateDto.setMemberPhone("11122223333");
       updateDto.setMemberAddress("서울특별시 테스트구 테스트로 123");
       updateDto.setSmsAgree(true);

       // when
       MemberMyPageResponseDto result = memberService.updateMyPage(memberNum, updateDto);

       // then
       assertThat(result).isNotNull();
       assertThat(result.getMemberPhone()).isEqualTo(updateDto.getMemberPhone());
       assertThat(result.getMemberAddress()).isEqualTo(updateDto.getMemberAddress());
       assertThat(result.isSmsAgree()).isEqualTo(updateDto.isSmsAgree());
   }
   
   // @Test
    void 로그인_성공_테스트() {
        MemberLoginRequestDto loginDto = new MemberLoginRequestDto("test@test.com", "1234");

        MemberLoginResponseDto result = memberService.login(loginDto);

        assertThat(result.getMemberId()).isEqualTo("test@test.com");
        assertThat(result.getMessage()).isEqualTo("로그인 성공");
        
        System.out.println(loginDto.toString());
        System.out.println(result.toString());
    }

    //@Test
    void 아이디_찾기_성공_테스트() {
        MemberEntity member = memberRepository.findByMemberNum(testMemberNum).get();

        String result = memberService.findMemberId(member.getMemberName(), member.getMemberPhone());

        assertThat(result).isEqualTo("test@test.com");
        
        System.out.println(member.toString());
        System.out.println(result.toString());
    }

    //@Test
    void 비밀번호_찾기_성공_테스트() {
        MemberEntity member = memberRepository.findByMemberNum(testMemberNum).get();

        String result = memberService.findMemberPw(
                member.getMemberId(), member.getMemberName(), member.getMemberPhone()
        );

        assertThat(result).contains("본인 확인");
        
        System.out.println(member.toString());
        System.out.println(result.toString());
    }
    
    //@Test
    @DisplayName("회원 비밀번호 변경 - 성공")
    void 회원_비밀번호_변경() {
        // given
        String memberId = "test2@test.com";

        // 테스트용 회원이 DB에 없으면 삽입
        memberRepository.findByMemberId(memberId);

        // 비밀번호 변경 DTO
        MemberPasswordUpdateRequestDto dto = new MemberPasswordUpdateRequestDto();
        dto.setMemberId(memberId);
        dto.setCurrentPassword("1234");
        dto.setNewPassword("dks123");
        dto.setNewPasswordCheck("dks123");

        // when
        memberService.updatePassword(dto);

        // then
        MemberEntity updated = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("회원 없음"));

        assertEquals("dks123", updated.getMemberPw());

        System.out.println("비밀번호 변경 성공: " + updated.getMemberPw());
    }

    //회원 sns 수신동의
    //@Test
//    void 회원_SMS수신동의_변경() {
//    	Long memberNum = 3L;
//        boolean smsAgree = false;
//        
//        MemberSmsAgreeUpdateRequestDto dto = new MemberSmsAgreeUpdateRequestDto();
//        dto.setSmsAgree(smsAgree); //요청값
//        
//        MemberSmsAgreeUpdateResponseDto result = memberService.updateSmsAgree(memberNum, dto);
//        
//        assertThat(result).isNotNull();
//        assertThat(result.getMemberNum()).isEqualTo(memberNum);
//        assertThat(result.isSmsAgree()).isEqualTo(smsAgree);
//        
//        System.out.println(result.getMessage());
//	}
    
    //@Test
    void 탈퇴_테스트() {
    	MemberDeleteDto result = memberService.memberOut(testMemberNum);

        assertThat(result.getMessage()).isEqualTo("회원 탈퇴 완료");
        assertThat(memberRepository.findByMemberNum(testMemberNum)).isEmpty();

        System.out.println("결과 메시지: " + result);
    }
    
    //@Test
    @DisplayName("회원 휴대폰 번호 중복 확인 테스트")
    void checkPhone() {
    	//입력한 핸드폰
//    	String registeredPhone = "01012222678";
//
//    	String result = memberService.checkPhoneNumber(registeredPhone);
//    	assertEquals("사용 가능한 번호입니다.", result); 
//    	System.out.println(result);
    	
    	//입력한 번호가 회원 핸드폰번호와 겹칠때.
    	String duPhone = "01012345672";
    	IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,() -> {
    	    memberService.checkPhoneNumber(duPhone);
    	});
    	assertEquals("이미 가입된 휴대폰 번호입니다.", ex.getMessage());
    	System.out.println(ex.getMessage());

    }
}