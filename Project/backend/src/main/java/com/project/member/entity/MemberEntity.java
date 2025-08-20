package com.project.member.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {
	
	@Id //기본키
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberNum; //회원번호

	@Column(name = "member_id")
    private String memberId; //이메일 아이디

    private String memberPw; //비밀번호
    
    private LocalDateTime pwUpdated; // 마지막 비밀번호 변경일
    
    private String memberName; //이름

    private LocalDate memberBirth; //생년월일

    @Column(name = "member_phone")
    private String memberPhone; //휴대폰 번호

    private String memberPostcode; // 우편번호
    private String memberRoadAddress; // 기본주소(도로명/지번)
    private String memberDetailAddress;  // 상세주소
    //@Deprecated
    private String memberAddress; //주소

    private LocalDate memberDay; //가입일시

    private Boolean memberLock; //계정 잠금 여부

    @Enumerated(EnumType.STRING)
    private MemberSex memberSex; //성별
    
    @Enumerated(EnumType.STRING) 
    private MemberState memberState; //회원상태
    
    private LocalDateTime outDate; // 회원이 OUT 상태로 변경된 순간 기록
    
    @Column(name = "sns_yn")
    private boolean smsAgree; //문자 수신여부(동의/비동의)

    //카카오 인증시 memberId로 저장되니까 이건 필요없을꺼 같은데
    private String kakaoId; //카카오아이디
    
    private String volSumtime;	//총 봉사시간
    
    private String accessToken;
    private String refreshToken;
    
    /* ─────────────────────────────────────────────────────────
    편의 메서드: 분리된 주소로부터 합친 주소 생성(표출용)
    - 프론트/DTO에서 필요 시 사용
    - 예: [28187] 충북 … 1239 공중화장실
    ───────────────────────────────────────────────────────── */
    public String buildFullAddress() {
    	final String p = safeTrim(memberPostcode);
    	final String r = safeTrim(memberRoadAddress);
    	final String d = safeTrim(memberDetailAddress);
    	StringBuilder sb = new StringBuilder();
    	if (!p.isEmpty()) sb.append('[').append(p).append("] ");
    	if (!r.isEmpty()) sb.append(r);
    	if (!d.isEmpty()) {
    		if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') sb.append(' ');
    		sb.append(d);
    	}
    	return sb.toString().trim();
    }

    /** 분리 주소 일괄 업데이트 */
    public void updateAddress(String postcode, String road, String detail) {
    	this.memberPostcode = safeTrim(postcode);
    	this.memberRoadAddress = safeTrim(road);
    	this.memberDetailAddress = safeTrim(detail);
    	// 레거시 필드도 함께 유지(점진 폐기 전까지)
    	this.memberAddress = buildFullAddress();
    }

    /** 레거시 문자열을 분리 컬럼에 반영(초기 1회 백필 등에 사용) */
    public void splitLegacyAddressIfNeeded() {
    	if ((isNotEmpty(memberPostcode) || isNotEmpty(memberRoadAddress) || isNotEmpty(memberDetailAddress)))
    		return; // 이미 분리되어 있으면 스킵

	final String legacy = safeTrim(memberAddress);
   	if (legacy.isEmpty()) return;
   		// 간단 파싱: "[12345] 기본 상세" 패턴만 1차 지원(정확한 분리는 서비스/배치에서 재정제)
   		String post = "";
   		String road = legacy;
   		String detail = "";
   		if (legacy.matches("^\\[[0-9]{5}\\].*")) {
			post = legacy.substring(1, 6);
			String rest = legacy.substring(7).trim();
			// 기본/상세 간 구분은 서비스단에서 더 정제하는 게 안전.
			// 여기선 마지막 공백 전까지를 기본으로 두지 않고, 일단 'rest 전체'를 기본주소로 채우고 상세는 비움.
     		road = rest;
   		}

   		this.memberPostcode = post;
   		this.memberRoadAddress = road;
   		this.memberDetailAddress = detail;
   	// 레거시 유지
   		this.memberAddress = buildFullAddress();
 	}

    // ── 내부 유틸 ─────────────────────────────────────────────
    private static String safeTrim(String s) { return s == null ? "" : s.trim(); }
    private static boolean isNotEmpty(String s) { return s != null && !s.isBlank(); }
}