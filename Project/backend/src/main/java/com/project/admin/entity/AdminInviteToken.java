package com.project.admin.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

//관리자 초대 토큰 조건
//초대받은 관리자만 회원가입 가능(1회용)
@Entity
@Table(name = "admin_invite_token")
@Getter @Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder

//targetEmail : 초대 대상 이메일. 초대 토큰에 저장되어 가입 시 이메일 일치 검증에 사용.
//ttlMinutes : 토큰 유효시간(분). 지정 없으면 서비스 기본값. 만료되면 사용 불가.
//memo : 운영자 메모.
//token : 1회용 초대 토큰. 가입 시 제출해야 하는 핵심 값.
//expiresAt : 토큰 만료 시각.
//valid : 토큰 검증 결과(유효/무효).
//message : 검증/처리 결과 설명.
//adminId : 생성할 관리자 로그인 ID.
//adminEmail : 관리자 이메일(초대 이메일과 일치 검증 권장/필수).
//adminPw : 비밀번호(서버에서 암호화 저장).
//adminName : 관리자 이름.
//adminPhone : 관리자 휴대폰 번호.
//memberNum : 기존 MemberEntity 연동이 필요할 때 사용하는 회원 번호.

public class AdminInviteToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invite_id")
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 128)
    private String token;                 // 초대 토큰 문자열(서명/랜덤 UUID 등)

    @Column(name = "target_email", nullable = false, length = 120)
    private String targetEmail;           // 초대 대상 이메일(화이트리스트 용도)

    @Column(name = "issued_by", nullable = false, length = 80)
    private String issuedBy;              // 발급자(슈퍼관리자 id)

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;      // 만료 시각

    @Column(name = "used_at")
    private LocalDateTime usedAt;         // 사용 완료 시각(null이면 미사용)

    @Column(name = "memo")
    private String memo;                  // 메모(선택)
    
    // 편의 메서드
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public void markUsed() {
        this.usedAt = LocalDateTime.now();
    }
}

