package com.project.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.admin.dto.AdminForcedDeleteDto;
import com.project.admin.dto.AdminLoginResponseDto;
import com.project.admin.dto.AdminMemberDateUpdateRequestDto;
import com.project.admin.dto.AdminMemberDetailResponseDto;
import com.project.admin.dto.AdminMemberListResponseDto;
import com.project.admin.dto.AdminPasswordUpdateRequestDto;
import com.project.admin.dto.AdminLoginRequestDto;
import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
import com.project.common.dto.PageRequestDto;
import com.project.common.dto.PageResponseDto;
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

	@Override
	//관리자 로그인
	public AdminLoginResponseDto login(AdminLoginRequestDto dto) {
		//아이디와 비밀번호로 회원 정보 조회
		AdminEntity admin = adminRepository.findByAdminIdAndAdminPw(dto.getAdminId(), dto.getPw())
			.orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
		
		//로그인 성공 시 필요한 정보 dto 반환
		return AdminLoginResponseDto.builder()
				.adminId(admin.getAdminId())
				.adminEmail(admin.getAdminEmail()) 	//관리자 이메일
				.adminPhone(admin.getAdminPhone())	//관리자 전화번호
				.connectData(admin.getConnectData())	//접속시간
				.message("관리자 로그인 성공")
				.accessToken("admin token")
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
		if(!admin.getAdminPw().equals(dto.getCurrentPassword())) {
			throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
		}
		//새 비밀번호와 비밀번호 확인 일치 여부
		if(!dto.getNewPassword().equals(dto.getNewPasswordCheck())) {
			throw new IllegalArgumentException("변경할 비밀번호가 일치하지 않습니다.");
		}
		//이전 비밀번호와 같은지 확인
		if(!dto.getCurrentPassword().equals(dto.getNewPassword())) {
			throw new IllegalArgumentException("이전과 동일한 비밀번호는 사용할 수 없습니다.");
		}
		//비밀번호 변경
		admin.setAdminPw(dto.getNewPassword());
		
		//변경된 값 저장하기
		adminRepository.save(admin); //저장
	}

	//회원목록 조회(관리자)
	@Override
    //여러명의 회원(각각 기본키 보유)을 한번에 조회하기 떄문에 List사용
    public List<AdminMemberListResponseDto> adminMemberList(){
		//MemberEntity 리스트를 조회하여, 프론트에 필요한 데이터만 AdminMemberListResponseDto 형태로 변환 후 반환		
		return memberRepository.findAll().stream()
				.map(member -> AdminMemberListResponseDto.builder()
						.memberNum(member.getMemberNum()) //회원 번호
						.memberId(member.getMemberId()) //회원 아이디(이메일)
						.memberName(member.getMemberName()) 
						
						.memberDay(member.getMemberDay() != null
								? member.getMemberDay().toString()
								: null)
						.memberState(member.getMemberState().name()) 
						.memberLock(Boolean.TRUE.equals(member.getMemberLock())) 
						.build() 
					)
				//변환된 dto를 리스트로 수집
				.collect(Collectors.toList());
				
	}
	
	//회원정보 상세보기(관리자)
	@Override
	public AdminMemberDetailResponseDto adminMemberDetailView(Long memberNum){
		//조회할 memberNum 기준으로 MemberEntity 조회 //일치하는 회원이 없을경우 메시지 출력
		MemberEntity member = memberRepository.findByMemberNum(memberNum)
				.orElseThrow(() ->  new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
		
		return AdminMemberDetailResponseDto.builder()
				.memberNum(member.getMemberNum())		//회원번호
				.memberId(member.getMemberId())			//회원 아이디(이메일)
				.memberName(member.getMemberName())		//회원 이름
				.memberBirth(member.getMemberBirth())	//생년월일
				.memberPhone(member.getMemberPhone())	//핸드폰 번호
				.memberAddress(member.getMemberAddress())	//주소
				.memberSex(member.getMemberSex())		//성별
				.memberLock(Boolean.TRUE.equals(member.getMemberLock())) //계정 잠금상태
				.snsYn(member.isSnsYn())	//sms수신여부
				.build();
				
	}
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//회원탈퇴(관리자기준)
	public AdminForcedDeleteDto adminMemberOut(Long memberNum){
		MemberEntity member = memberRepository.findByMemberNum(memberNum)
				.orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
		
		//MemberState 상태만 OUT로 변경
		member.setMemberState(MemberState.OUT);
		//DB에서 바로 삭제(영구삭제)
		//memberRepository.delete(member);
		
		return new AdminForcedDeleteDto(member.getMemberNum(), "회원 탈퇴 완료");
	}

	@Override
	public AdminMemberDateUpdateRequestDto adminMemberUpdateView(Long memberNum) {
		MemberEntity member = memberRepository.findByMemberNum(memberNum)
				.orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
		
		return AdminMemberDateUpdateRequestDto.builder()
				.memberLock(Boolean.TRUE.equals(member.getMemberLock()))
				.memberState(member.getMemberState())
				.build();
	}

	
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
