package com.project.member.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
//비밀번호 단뱡향 복호화
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
 
import com.project.common.jwt.JwtTokenProvider;
import com.project.common.util.JasyptUtil;
import com.project.member.dto.AddressUpdateRequestDto;
import com.project.member.dto.KakaoSignUpRequestDto;
import com.project.member.dto.KakaoUserInfoDto;
import com.project.member.dto.MemberAuthResult;
import com.project.member.dto.MemberDeleteDto;
import com.project.member.dto.MemberIdCheckResponseDto;
import com.project.member.dto.MemberLoginRequestDto;
import com.project.member.dto.MemberLoginResponseDto;
import com.project.member.dto.MemberMeResponseDto;
import com.project.member.dto.MemberMyPageResponseDto;
import com.project.member.dto.MemberMyPageUpdateRequestDto;
import com.project.member.dto.MemberSignUpRequestDto;
import com.project.member.dto.MemberSignUpResponseDto;
import com.project.member.dto.PhoneUpdateRequestDto;
import com.project.member.dto.ResetPasswordUpdateRequestDto;
import com.project.member.dto.SelfPasswordUpdateRequestDto;
import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberSex;
import com.project.member.entity.MemberState;
import com.project.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service //tjqltmrPcmd(spring bean)으로 등록
@RequiredArgsConstructor //final로 선언된 memberRepository를 자동으로 생성자 주입 시켜줌
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final KakaoApiService kakaoApiService;
	private final JwtTokenProvider jwtTokenProvider;
	
	//회원가입
	@Transactional // 하나의 트랜잭션으로 처리(중간 에러 시 전체 롤백)
	@Override
	public MemberSignUpResponseDto sigup(MemberSignUpRequestDto dto) {

	    // ─────────────────────────────────────────────────────────────
	    // 0) 입력 정규화 및 카카오 가입 여부 판별
	    //   - 이메일은 소문자/trim 정규화
	    //   - kakaoId 존재 시 소셜 가입으로 분기 (비밀번호 null 저장)
	    // ─────────────────────────────────────────────────────────────
	    final String emailRaw = dto.getMemberId();
	    final String email = (emailRaw == null ? "" : emailRaw.trim().toLowerCase());

	    final String kakaoIdRaw = dto.getKakaoId();
	    final String kakaoId = (kakaoIdRaw == null ? "" : kakaoIdRaw.trim());
	    final boolean isKakaoSignup = !kakaoId.isEmpty();

	    // ─────────────────────────────────────────────────────────────
	    // 1) 2차 방어: 아이디(이메일) 중복 체크
	    //    - 기존 로직 유지 (existsByMemberId)
	    // ─────────────────────────────────────────────────────────────
	    if (memberRepository.existsByMemberId(email)) {
	        throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
	    }

	    // (옵션) 이메일 형식 간단 검증
	    if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
	        throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
	    }

	    // ─────────────────────────────────────────────────────────────
	    // 1-2) 카카오 가입일 경우: kakaoId 중복 방지
	    //     - findFirstByKakaoId(...) 메서드가 Repository에 있어야 합니다.
	    // ─────────────────────────────────────────────────────────────
	    if (isKakaoSignup && memberRepository.findFirstByKakaoId(kakaoId).isPresent()) {
	        throw new IllegalArgumentException("이미 카카오 계정으로 가입된 사용자입니다.");
	    }
	 // ─────────────────────────────────────────────────────────────
	 // 2) 비밀번호 처리
	//	     - 일반 가입: 평소대로 암호화
	//	     - 카카오 가입: 화면 입력 없이 가입 → DB NOT NULL 만족 위해 난수 해시 저장
		 // ─────────────────────────────────────────────────────────────
		 final String encodedPw;
		 if (isKakaoSignup) {
		     // member_pw NOT NULL 제약 회피 + 실제로는 사용할 수 없는 랜덤 비번
		     String randomRaw = "kakao:" + java.util.UUID.randomUUID() + ":" + System.nanoTime();
		     encodedPw = passwordEncoder.encode(randomRaw);
		 } else {
		     if (dto.getMemberPw() == null || dto.getMemberPw().isBlank()) {
		         throw new IllegalArgumentException("비밀번호를 입력해주세요.");
		     }
		     encodedPw = passwordEncoder.encode(dto.getMemberPw());
		 }

	    // ─────────────────────────────────────────────────────────────
	    // 3) 휴대폰번호 암호화 (기존 로직 + 숫자만 보정)
	    //    - 카카오/일반 관계없이 숫자만 추출 후 Jasypt 암호화
	    // ─────────────────────────────────────────────────────────────
	    final String phoneDigits = dto.getMemberPhone() == null
	            ? null
	            : dto.getMemberPhone().replaceAll("[^0-9]", ""); // 숫자만
	    final String encryptedPhone = JasyptUtil.encrypt(phoneDigits);

	    // ─────────────────────────────────────────────────────────────
	    // 4) 엔티티 변환 및 저장 (기존 필드 유지 + kakaoId 분기 세팅)
	    //    - memberState: 기본 ACTIVE
	    //    - memberLock: false
	    //    - smsAgree: 기존 dto.isSmsAgree() 그대로 사용
	    //    - kakaoId: 카카오 가입이면 값 세팅, 일반 가입이면 null
	    // ─────────────────────────────────────────────────────────────
	    MemberEntity newMember = MemberEntity.builder()
	            .memberId(email)                            // (정규화된 이메일)
	            .memberPw(encodedPw)                        // (카카오면 null)
	            .memberName(dto.getMemberName())
	            .memberBirth(dto.getMemberBirth())
	            .memberPhone(encryptedPhone)                // (암호화 저장)
	            .memberAddress(dto.getMemberAddress())
	            .memberDay(LocalDate.now())
	            .memberSex(dto.getMemberSex())
	            .memberState(MemberState.ACTIVE)            // 기본 상태
	            .memberLock(false)
	            .smsAgree(dto.isSmsAgree())
	            .kakaoId(isKakaoSignup ? kakaoId : null)    // 핵심: 소셜 연동 키
	            .build();

	    MemberEntity saved = memberRepository.save(newMember);

	    // ─────────────────────────────────────────────────────────────
	    // 5) 응답 DTO 반환 (기존 형식 유지)
	    // ─────────────────────────────────────────────────────────────
	    return new MemberSignUpResponseDto(
	            null,                      // 필요 시 리턴 필드 확장
	            saved.getMemberId(),       // 가입된 이메일(ID)
	            "회원가입 완료"
	    );
	}

	//아이디 중복체크
	@Override
	public MemberIdCheckResponseDto checkDuplicateMemberId(String memberId) {
	    boolean exists = memberRepository.existsByMemberId(memberId);
	    String message = exists ? "사용할 수 없는 아이디입니다." : "사용 가능한 아이디입니다.";
	    return new MemberIdCheckResponseDto(exists, message);
	}
	//아이디 중복체크 true false
   @Override
    public boolean isDuplicatedMemberId(String memberId) {
        // 방어코드: null/blank는 중복 아님으로 처리(컨트롤러에서 이미 막지만 한 번 더)
        if (memberId == null || memberId.isBlank()) return false;
        return memberRepository.existsByMemberId(memberId);
    }

	//로그인
	@Override
    public MemberAuthResult authenticate(MemberLoginRequestDto dto) {
        // 1) 회원 조회
        MemberEntity member = memberRepository.findByMemberId(dto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));

        // 2) 상태 차단 (OUT/LOCK)
        if (member.getMemberState() == MemberState.OUT || Boolean.TRUE.equals(member.getMemberLock())) {
            throw new IllegalStateException("탈퇴(또는 잠금) 처리된 계정입니다. 관리자에게 문의하세요.");
        }

        // 3) 비밀번호 검증
        //  - 주의: 요청 DTO 필드명은 memberPw 사용
        if (!passwordEncoder.matches(dto.getMemberPw(), member.getMemberPw())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // 4) 통과 → 기본 정보만 리턴 (토큰 발급/저장은 Controller에서)
        return MemberAuthResult.builder()
                .memberNum(member.getMemberNum())
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .build();
    }

    @Override
    public boolean isPasswordExpired(MemberEntity member) {
        // 네 기존 로직 유지
        // 예: pwUpdated 기준으로 n일 경과 판단 등
        return false;
    }
	 
	// 설명: memberId로 조회해 마이페이지에 필요한 최소 정보 반환
	@Transactional
    @Override
    public MemberMeResponseDto getMyInfo(String memberId) {
        MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        return MemberMeResponseDto.builder()
                .memberNum(member.getMemberNum())
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .memberState(member.getMemberState() != null ? member.getMemberState().name() : null)
                .build();
    }
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//마이페이지
	public MemberMyPageResponseDto myPage(Long memberNum) {
		MemberEntity member = memberRepository.findByMemberNum(memberNum)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));
		
		//핸드폰번호 복호화
		String decryptedPhone = JasyptUtil.decrypt(member.getMemberPhone());
        return MemberMyPageResponseDto.builder()
				.memberName(member.getMemberName())
				.memberId(member.getMemberId())
				.memberBirth(member.getMemberBirth())
				.memberSex(member.getMemberSex()) //enum은 그대로 호출
				// ✅ 분리 주소 그대로 제공
                .memberPostcode(member.getMemberPostcode())
                .memberRoadAddress(member.getMemberRoadAddress())
                .memberDetailAddress(member.getMemberDetailAddress())
                //합쳐진 주소
				.memberAddress(member.getMemberAddress())
				.memberPhone(decryptedPhone)
//				.memberPhone(phoneForOwner)
				.kakaoId(member.getKakaoId())
				.smsAgree(member.isSmsAgree()) //boolean타입은 is로 호출
				.build();
	}
	//마이페이지 수정 + sms 동의
	@Transactional
	@Override
	public MemberMyPageResponseDto updateMyPage(Long memberNum, MemberMyPageUpdateRequestDto dto) {
	    MemberEntity member = memberRepository.findByMemberNum(memberNum)
	            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

	    //전화번호 암호화 처리
	    if(dto.getMemberPhone() != null) {
	    	String encryptedPhone = JasyptUtil.encrypt(dto.getMemberPhone()); //암호화
	    	//본인 제외 핸드폰번호 중복 검사
	    	Optional<MemberEntity> existing = memberRepository.findByMemberPhone(encryptedPhone);
	    	if(existing.isPresent() && !existing.get().getMemberNum().equals(memberNum)){
	    		throw new IllegalArgumentException("사용할 수 없는 핸드폰 번호입니다.");
	    	}
	    	member.setMemberPhone(encryptedPhone);	//암호화된 (수정된) 핸드폰번호 저장
	    }
	    
	    // 수정 가능한 항목만 반영
	    member.setMemberName(dto.getMemberName());
	    member.setMemberSex(dto.getMemberSex());
	    member.setMemberAddress(dto.getMemberAddress());
	    member.setSmsAgree(dto.isSmsAgree()); //체크박스 상태 반영

	    return MemberMyPageResponseDto.builder()
	            .memberName(member.getMemberName())
	            .memberId(member.getMemberId())
	            .memberBirth(member.getMemberBirth())
	            .memberSex(member.getMemberSex())
	            .memberAddress(member.getMemberAddress())
	            .memberPhone(member.getMemberPhone())
	            .kakaoId(member.getKakaoId())
	            .smsAgree(member.isSmsAgree())
	            .build();
	}

	//주소 변경하기
	@Transactional
    @Override
    public MemberMyPageResponseDto updateMyAddress(Long memberNum, AddressUpdateRequestDto dto) {
        MemberEntity member = memberRepository.findByMemberNum(memberNum)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));

        // ✅ 분리 주소로 갱신(+ 레거시 필드 동기화)
        member.updateAddress(dto.getPostcode(), dto.getRoadAddress(), dto.getDetailAddress());
        // JPA dirty checking으로 업데이트

        // 갱신 후 최신 데이터로 응답
        return myPage(memberNum);
    }

	//휴대폰 번호 변경
	@Transactional
	@Override
	public MemberMyPageResponseDto updateMyPhone(Long memberNum, PhoneUpdateRequestDto dto) {
	    MemberEntity m = memberRepository.findByMemberNum(memberNum)
	        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));

	    // 새 번호: 숫자만
	    final String newDigits = (dto.getPhone() == null ? "" : dto.getPhone().replaceAll("[^0-9]", ""));
	    if (newDigits.length() < 10 || newDigits.length() > 11) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "휴대전화 번호 형식이 올바르지 않습니다.");
	    }
	    // 현재 번호(저장값) 복호화 → 숫자만
	    String currentDigits = "";
	    try {
	        String curRaw = m.getMemberPhone();
	        if (curRaw != null && !curRaw.isBlank()) {
	            String dec = curRaw.startsWith("ENC(") && curRaw.endsWith(")") ? JasyptUtil.decrypt(curRaw) : curRaw;
	            currentDigits = dec.replaceAll("[^0-9]", "");
	        }
	    } catch (Exception e) {
	        // 복호화 실패는 기존값 비교를 못하더라도 업데이트는 가능하도록 넘어감(로그만)
	        log.warn("phone decrypt failed: {}", e.toString());
	    }
	    // ✅ 동일 번호면 NO-OP
	    if (!currentDigits.isEmpty() && currentDigits.equals(newDigits)) {
	        return myPage(memberNum);
	    }
	    // 저장(암호화)
	    try {
	        String encrypted = JasyptUtil.encrypt(newDigits);
	        m.setMemberPhone(encrypted);
	        // JPA dirty checking
	    } catch (DataIntegrityViolationException dive) {
	        // 유니크 제약 위반 등 → 409
	        throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 휴대전화 번호입니다.");
	    }
	    return myPage(memberNum);
	}
	
	//sns 동의 변경(회원정보수정)
	@Transactional
	@Override
	public MemberMyPageResponseDto updateMySmsAgree(Long memberNum, boolean smsAgree) {
	    MemberEntity m = memberRepository.findByMemberNum(memberNum)
	        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다"));

	    // 변경 없으면 NO-OP 응답
	    if (m.isSmsAgree() == smsAgree) {
	        return myPage(memberNum);
	    }

	    m.setSmsAgree(smsAgree); // JPA dirty checking으로 업데이트
	    return myPage(memberNum);
	}
	
 	@Transactional
    @Override
    public MemberDeleteDto memberOut(Long memberNum, String requesterId, String message) {
        MemberEntity member = memberRepository.findByMemberNum(memberNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        // ✅ 본인 확인: 로그인 주체와 대상 회원이 동일해야 함
        if (requesterId == null || !requesterId.equals(member.getMemberId())) {
            throw new SecurityException("본인만 탈퇴할 수 있습니다.");
        }

        // ✅ 이미 OUT이면 그대로 응답
        if (member.getMemberState() == MemberState.OUT) {
            return new MemberDeleteDto(member.getMemberNum(), member.getMemberName(), "이미 탈퇴 처리된 회원입니다.");
        }

        // === 권장 정책: 논리삭제(OUT) ===
        member.setMemberState(MemberState.OUT); // 상태 OUT 전환
        member.setMemberLock(true);             // 로그인 차단

        return new MemberDeleteDto(
                member.getMemberNum(),
                member.getMemberName(),
                (message == null || message.isBlank()) ? "회원 탈퇴(OUT) 처리 완료" : message
        );

        /* === 만약 물리삭제를 고집한다면, 위 논리삭제 대신 아래 블록 사용 ===
        try {
            memberRepository.delete(member); // 물리 삭제
            return new MemberDeleteDto(member.getMemberNum(), member.getMemberName(), "회원 물리삭제 완료");
        } catch (DataIntegrityViolationException e) {
            // FK 제약 등으로 삭제 실패 → OUT 전환 폴백
            member.setMemberState(MemberState.OUT);
            member.setMemberLock(true);
            return new MemberDeleteDto(member.getMemberNum(), member.getMemberName(),
                    "관련 데이터로 즉시 삭제 불가 → 탈퇴(OUT) 상태로 전환됨");
        }
        */
    }
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//아이디 찾기
	public String findMemberId(String memberName, String memberPhone) {
		
		String encryptedPhone = JasyptUtil.encrypt(memberPhone);
		
		return memberRepository.findByMemberNameAndMemberPhone(memberName, encryptedPhone)
				.map(member -> "회원님의 ID는 " + member.getMemberId() + " 입니다.")
				.orElseThrow(() -> new IllegalArgumentException("일치하는 회원이 없습니다."));
		
	}
	
	@Transactional //하나의 트랜잭션으로 처리함(중간에 오류나면 전체 롤백)
	@Override
	//비밀번호 찾기
	public String findMemberPw(String memberId, String memberName, String memberPhone) {
		
		String encryptedPhone = JasyptUtil.encrypt(memberPhone);
		
		MemberEntity member = memberRepository
				.findByMemberIdAndMemberNameAndMemberPhone(memberId, memberName, encryptedPhone)
				.orElseThrow(() -> new IllegalArgumentException("입력하신 정보와 일치하는 회원이 없습니다."));
		
		return "본인 확인이 완료되었습니다. 비밀번호를 재설정 해주세요";
	}
	

	
	//휴대폰 번호로 회원 존재 여부 확인
	public String checkPhoneNumber(String phoneNum) {
	    String encryptedPhone;
		//memberPhone컬럼에 phoneNum와 같은 값이 존재하는지 조회
	    try {
	    	//입력값을 암호화
	        encryptedPhone = JasyptUtil.encrypt(phoneNum);
	        System.out.println("📦 암호화된 입력값: " + encryptedPhone); // 🔍 여기에 로그 찍기
	    } catch (Exception e) {
	        throw new RuntimeException("휴대폰 번호 확인중 암호화 오류 발생", e);
	    }
	    //암호화된 값으로 조회
	    boolean exists = memberRepository.findByMemberPhone(encryptedPhone).isPresent();

	    //동일한 값이 존재한다면 예외 발생
	    if (exists) {
	        throw new IllegalArgumentException("이미 가입된 휴대폰 번호입니다.");
	    }
	    //존재하지 않으면 인증가능
	    return "사용 가능한 번호입니다.";
	    
		//1. 사용자가 핸드폰번호 입력
  		//2. encrypt 핸드폰번호 암호화
  		//3. 암호화된 문자열을 memberPhone과 비교
  		//4. 존재여부 판단 > 중복 확인 처리
	}
	
	
    //마이페이지 비밀번호 변경
    @Transactional
    @Override
    public void updatePasswordSelf(Long memberNum, SelfPasswordUpdateRequestDto dto) {
        MemberEntity m = memberRepository.findByMemberNum(memberNum)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."));

        // ✅ 카카오 회원은 비밀번호 변경 불가
        if (m.getKakaoId() != null && !m.getKakaoId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "카카오회원은 비밀번호 변경을 사용하실 수 없습니다.");
        }
        
        if (dto.getNewPassword() == null || !dto.getNewPassword().equals(dto.getNewPasswordCheck())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "새 비밀번호와 확인이 일치하지 않습니다.");
        }

        if (!passwordEncoder.matches(dto.getCurrentPassword(), m.getMemberPw())) {
            // 프론트 요구: 현재 비밀번호 오류 우선 안내
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다.");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), m.getMemberPw())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이전과 동일한 비밀번호는 사용할 수 없습니다.");
        }

        m.setMemberPw(passwordEncoder.encode(dto.getNewPassword()));
        // (선택) m.setPasswordChangedAt(Instant.now());
    }

    //비밀번호 찾기 비밀번호 변경
    @Transactional
    @Override
    public void resetPassword(ResetPasswordUpdateRequestDto dto) {
        if (dto.getMemberId() == null || dto.getMemberId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "회원 식별 정보가 없습니다.");
        }
        if (dto.getNewPassword() == null || !dto.getNewPassword().equals(dto.getNewPasswordCheck())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "새 비밀번호와 확인이 일치하지 않습니다.");
        }

        // TODO: resetToken 검증 로직(서버 저장된 토큰/만료 확인) 추가 권장
        MemberEntity m = memberRepository.findByMemberId(dto.getMemberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."));

        if (passwordEncoder.matches(dto.getNewPassword(), m.getMemberPw())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이전과 동일한 비밀번호는 사용할 수 없습니다.");
        }

        m.setMemberPw(passwordEncoder.encode(dto.getNewPassword()));
        // (선택) 비번 만료 해제, 토큰 소거 등 후처리
    }
  



    /**
     * 카카오 로그인 처리 (회원가입 제거 버전)
     * 
     * @param code 카카오에서 리다이렉트로 전달한 인가코드
     * @return 기존 회원이면 JWT 토큰 포함 MemberLoginResponseDto
     * @throws Exception 카카오 API 오류, 회원 미존재 시 예외
     */
    @Override
    public MemberLoginResponseDto handleKakaoLogin(String code) throws Exception {
        // 1) 인가코드 → access_token
        String accessToken = kakaoApiService.getAccessToken(code);

        // 2) access_token → 사용자 정보
        KakaoUserInfoDto userInfo = kakaoApiService.getUserInfo(accessToken);

        // 3) 카카오 ID 기반 회원 조회
        Optional<MemberEntity> existing = memberRepository.findByKakaoId(userInfo.getKakaoId());

        if (existing.isEmpty()) {
            // 🚫 회원가입 로직 제거 → 미연동 시 바로 예외 던짐
            throw new IllegalStateException("카카오 계정이 연결된 회원이 없습니다.");
        }

        MemberEntity member = existing.get();

        // 4) JWT 발급
        String jwtAccess = jwtTokenProvider.generateAccessToken(member.getMemberId());
        String jwtRefresh = jwtTokenProvider.generateRefreshToken(member.getMemberId());

        // 5) 로그인 성공 DTO 반환
        return MemberLoginResponseDto.builder()
                .memberId(member.getMemberId())
                .memberName(member.getMemberName())
                .accessToken(jwtAccess)
                .refreshToken(jwtRefresh)
                .build();
    }

     /**
      * 카카오 birthyear("1995"), birthday("0214") → "1995-02-14"
      */
     private String parseBirth(String year, String mmdd) {
         if (year == null || mmdd == null || year.isBlank() || mmdd.isBlank()) {
             return null;
         }
         String month = mmdd.substring(0, 2);
         String day = mmdd.substring(2, 4);
         return year + "-" + month + "-" + day;
     }

     /**
      * "+82 10-1234-5678" → "01012345678"
      */
     private String formatPhone(String raw) {
         if (raw == null) return null;
         String digits = raw.replaceAll("[^0-9]", "");
         if (digits.startsWith("82")) {
             digits = digits.substring(2); // "8210..." → "10..."
         }
         if (digits.startsWith("10")) {
             digits = "0" + digits;
         }
         return digits;
     }
     @Override
     public MemberEntity findByMemberNum(Long memberNum) {
         // 회원 번호로 회원을 찾고, 없으면 예외 발생
         return memberRepository.findByMemberNum(memberNum)
                 .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
     }


     @Override
     public Optional<MemberEntity> findByMemberId(String memberId) {
         return memberRepository.findByMemberId(memberId);
     }

     
     
     

 }

    