package com.project.admin.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.admin.dto.AdminLoginRequestDto;
import com.project.admin.dto.AdminLoginResponseDto;
import com.project.admin.dto.AdminMemberDetailResponseDto;
import com.project.admin.dto.AdminMemberListResponseDto;
import com.project.admin.dto.AdminMemberUpdateRequestDto;
import com.project.admin.dto.AdminPasswordUpdateRequestDto;
import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
import com.project.common.jwt.JwtTokenProvider;
import com.project.common.util.JasyptUtil;
import com.project.member.dto.MemberPageRequestDto;
import com.project.member.dto.MemberPageResponseDto;
import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberState;
import com.project.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service //tjqltmrPcmd(spring bean)으로 등록
@Transactional
@RequiredArgsConstructor //final로 선언된 memberRepository를 자동으로 생성자 주입 시켜줌
public class AdminServiceImpl implements AdminService {

	private final AdminRepository adminRepository;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	//복호화 고정키값 and 테스트 키값 ☆필수★
	//추후 변경
    static {
        System.setProperty("JASYPT_ENCRYPTOR_PASSWORD", "test-key");
    }
    
    @Transactional
    @Override
    // 관리자 로그인: ID 고정(옵션), 비밀번호 매칭, 토큰 발급/저장, 응답 DTO 구성
	public AdminLoginResponseDto login(AdminLoginRequestDto dto) {
		
	    if (!dto.getAdminId().equals("admin")) {
	        throw new AccessDeniedException("지정된 관리자 계정만 로그인할 수 있습니다.");
	    }
	    
	    //관리자 ID 기준으로 먼저 조회
	    AdminEntity admin = adminRepository.findFirstByAdminId(dto.getAdminId())
	        .orElseThrow(() -> new IllegalArgumentException("아이디가 일치하지 않습니다."));
	    
		//암호화된 비밀번호 비교
		if(!passwordEncoder.matches(dto.getAdminPw(), admin.getAdminPw())){
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}
		
		//역할: ADMIN 고정
	    String accessToken = jwtTokenProvider.generateAccessToken(admin.getAdminId(), "ADMIN");
	    String refreshToken = jwtTokenProvider.generateRefreshToken(admin.getAdminId());

	    //Refresh 토큰 DB 저장
	    admin.setRefreshToken(refreshToken);
	    adminRepository.save(admin);
	    
		//로그인 성공 시 필요한 정보 dto 반환
		return AdminLoginResponseDto.builder()
				.adminId(admin.getAdminId())
				.adminPhone(admin.getAdminPhone())	//관리자 전화번호
				.connectData(admin.getConnectData())	//접속시간
				.message("관리자 로그인 성공")
				.accessToken("정상 토큰")
				.refreshToken("재발급 토큰")
				.build();
	}
	

	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//비밀번호 변경
	public void updatePassword(String adminId, AdminPasswordUpdateRequestDto dto) {
		//아이디로 관리자 확인
		AdminEntity admin = adminRepository.findFirstByAdminId(adminId)
				.orElseThrow(() -> new IllegalArgumentException("관리자 계정에 문제가 발생했습니다."));
		
		//현재 비밀번호 검증
		if(!passwordEncoder.matches(dto.getCurrentPassword(), admin.getAdminPw())) {
			throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
		}
		//새 비밀번호와 비밀번호 확인 일치 여부
		if(!dto.getNewPassword().equals(dto.getNewPasswordCheck())) {
			throw new IllegalArgumentException("변경할 비밀번호가 일치하지 않습니다.");
		}
		//이전 비밀번호와 같은지 확인
		if(passwordEncoder.matches(dto.getNewPassword(), admin.getAdminPw())) {
			throw new IllegalArgumentException("이전과 동일한 비밀번호는 사용할 수 없습니다.");
		}
		
		//비밀번호 변경
		admin.setAdminPw(passwordEncoder.encode(dto.getNewPassword()));
		
		//변경된 값 저장하기
		adminRepository.save(admin); //저장
	}

	//관리자용 회원 목록 조회 (페이징 + 검색 포함)
	//param pageRequestDto page, size, keyword 등을 포함한 요청 DTO
	//return PageResponseDto<AdminMemberListResponseDto>
	@Override
	public MemberPageResponseDto<AdminMemberListResponseDto> getMemberList(MemberPageRequestDto req) {
	    //Pageable pageable = req.toPageable(Sort.by("memberNum").descending());	//내림차순
		Pageable pageable = req.toPageable(Sort.by("memberNum").ascending());	//오름차순

	    Page<MemberEntity> result;
	    
	    if (req.getKeyword() != null && !req.getKeyword().isEmpty()) {
	        result = memberRepository.findByMemberNameContaining(req.getKeyword(), pageable);
	    } else {
	        result = memberRepository.findAll(pageable);
	    }

	    List<AdminMemberListResponseDto> content = result.getContent().stream()
	            .map(this::toDto)
	            .toList();

	    return new MemberPageResponseDto<>(
	            content,
	            result.getNumber(),
	            result.getSize(),
	            result.getTotalPages(),
	            result.getTotalElements()
	    );
	} 

	private AdminMemberListResponseDto toDto(MemberEntity entity) {
	    return new AdminMemberListResponseDto(
	        entity.getMemberNum(),
	        entity.getMemberId(),
	        entity.getMemberName(),
	        entity.getMemberDay() != null ? entity.getMemberDay().toString() : null, // LocalDateTime → String 변환 필요 시
	        entity.getMemberState().name(),   // enum이면 .name()
	        entity.getMemberLock()             // boolean 필드
	    );
	}
	
//	public PageResponseDto<AdminMemberListResponseDto> getMemberList(PageRequestDto pageRequestDto) {
//	    //page, size, sort 정보를 PageRequest로 변환
//	    Pageable pageable = pageRequestDto.toPageable();
//	    Page<MemberEntity> result;
//
//	    try {
//	    //검색 키워드가 있을 경우, 이름 기준 부분 일치 검색
//	    if (pageRequestDto.getKeyword() != null && !pageRequestDto.getKeyword().isBlank()) {
//	        result = memberRepository.findByMemberNameContaining(pageRequestDto.getKeyword(), pageable);
//	    } else {
//	        //검색 키워드가 없으면 전체 회원 목록 조회
//	        result = memberRepository.findAll(pageable);
//	    }
//
//	    //Entity → DTO 변환 (프론트에 필요한 정보만 추출)
//	    List<AdminMemberListResponseDto> dtoList = result.getContent().stream()
//	            .map(member -> AdminMemberListResponseDto.builder()
//	                    .memberNum(member.getMemberNum())                             //회원 고유번호
//	                    .memberId(member.getMemberId())                              //아이디
//	                    .memberName(member.getMemberName())                          //이름
//	                    .memberDay(member.getMemberDay().toString())                 //가입일(LocalDate → String)
//	                    .memberState(member.getMemberState().name())                 //상태(ENUM → 문자열)
//	                    .memberLock(Boolean.TRUE.equals(member.getMemberLock()))     //계정 잠금 여부
//	                    .build())
//	            .toList();
//
//	    //Page 정보와 함께 결과 묶어서 반환
//	    return PageResponseDto.<AdminMemberListResponseDto>builder()
//	            .content(dtoList)                            //현재 페이지 회원 목록
//	            .currentPage(result.getNumber() + 1)         //현재 페이지 번호 (0 → 1 보정)
//	            .totalPages(result.getTotalPages())          //전체 페이지 수
//	            .totalElements(result.getTotalElements())    //전체 회원 수
//	            .isFirst(result.isFirst())                   //첫 페이지 여부
//	            .isLast(result.isLast())                     //마지막 페이지 여부
//	            .build();
//	    } catch (Exception ex) {
//            // ✅ 어디서 터지는지 로그로 반드시 확인
//            // 임시로 빈 페이지 리턴해서 프론트 막힘 해소 (로그로 원인 추적)
//            return PageResponseDto.<AdminMemberListResponseDto>builder()
//                    .content(Collections.emptyList())
//                    .currentPage(1)
//                    .totalPages(1)
//                    .totalElements(0)
//                    .isFirst(true)
//                    .isLast(true)
//                    .build();
//        }
//    }
	
	/** null 이면 "-" 로 */
	private String safe(String v) {
	    return (v == null || v.isBlank()) ? "-" : v;
	}
	
	//회원정보 상세보기(관리자)
	@Override
	public AdminMemberDetailResponseDto adminMemberDetailView(Long memberNum){
		//조회할 memberNum 기준으로 MemberEntity 조회 //일치하는 회원이 없을경우 메시지 출력
		MemberEntity member = memberRepository.findByMemberNum(memberNum)
				.orElseThrow(() ->  new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
		
		String decryptedPhone = JasyptUtil.decrypt(member.getMemberPhone());
		
		return AdminMemberDetailResponseDto.builder()
				.memberNum(member.getMemberNum())		//회원번호
				.memberId(member.getMemberId())			//회원 아이디(이메일)
				.memberName(member.getMemberName())		//회원 이름
				.memberBirth(member.getMemberBirth())	//생년월일
				.memberPhone(decryptedPhone)	//핸드폰 번호
				.memberAddress(member.getMemberAddress())	//주소
				.memberSex(member.getMemberSex())		//성별
				.memberLock(Boolean.TRUE.equals(member.getMemberLock())) //계정 잠금상태
				.smsAgree(member.isSmsAgree())	//sms수신여부
				.build();
				
	}
	

	//회원정보수정(관리자기준)
	//param : memberNum 회원 고유번호
	//param : memberState 상태(ACTIVE, REST, OUT)
	//param : memberLock 계정 잠금 여부
	//return 수정된 상태 정보를 담은 DTO
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	public AdminMemberUpdateRequestDto adminMemberStateChange(Long memberNum, MemberState memberState, Boolean memberLock) {
		//회원 조회
		MemberEntity member = memberRepository.findByMemberNum(memberNum)
				.orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
		
		//회원상태 선택한 상태로 변경
		member.setMemberState(memberState);
		//상태가 OUT일경우 outDate에 기록
		if(memberState == MemberState.OUT) {
			member.setOutDate(LocalDateTime.now());//탈퇴일자 저장
		}else {
			member.setOutDate(null);//상태 복구 시 탈퇴일자 초기화
		}
		//계정 잠금 여부 설정
		member.setMemberLock(memberLock);
		memberRepository.save(member);
		 
		//회원상태 변경시 메시지 처리
		String stateMsg;
		switch(memberState) {
			case OUT -> stateMsg = "회원 탈퇴 상태로 변경 완료";
			case REST -> stateMsg = "회원 휴먼 상태로 변경 완료";
			case ACTIVE -> stateMsg = "정상 회원 상태로 변경 완료";
			default -> stateMsg = "회원 상태가 성공적으로 변경되었습니다.";
		}
		//계정잠금 변경시 메시지 처리
		String lockMsg = memberLock ? "계정이 잠금 상태로 변경되었습니다." : "계정 잠금이 해제되었습니다.";
		String resultMessage = stateMsg + "/" + lockMsg;
		
		return AdminMemberUpdateRequestDto.builder()
				.memberState(member.getMemberState())
				.memberLock(member.getMemberLock())
				.message(resultMessage)
				.build();
	}
	@Override
    public Optional<AdminEntity> findByAdminId(String adminId) {
        return adminRepository.findFirstByAdminId(adminId);
    }
	
}
