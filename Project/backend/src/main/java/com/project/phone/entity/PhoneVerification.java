package com.project.phone.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 휴대폰 인증번호 저장 엔티티
 * - purpose: SIGNUP(회원가입), UPDATE(마이페이지 전화번호 변경)
 * - verified: 검증 완료 여부
 * - used: (선택) 전화번호 변경 완료 시 소모 처리 용도
 */
@Entity
@Table(name = "PHONE_VERIFICATION",
       indexes = {
         @Index(name = "idx_pv_phone", columnList = "phoneNumber"),
         @Index(name = "idx_pv_purpose", columnList = "purpose"),
         @Index(name = "idx_pv_expire", columnList = "expiresAt")
       })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PhoneVerification {

    @Id
    @Column(length = 36)
    private String id; // UUID 문자열

    @Column(nullable = false, length = 20)
    private String phoneNumber; // 수신자(회원) 전화번호 (숫자만 저장 권장)

    @Column(nullable = false, length = 6)
    private String code; // 6자리 인증코드(문자열, 앞자리 0 허용)

    @Column(nullable = false, length = 10)
    private String purpose; // "SIGNUP" or "UPDATE"

    @Column(nullable = false)
    private LocalDateTime expiresAt; // 만료시각

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성시각

    private LocalDateTime verifiedAt; // 검증 완료 시각

    @Column(nullable = false)
    private boolean verified; // true면 코드 검증 끝난 상태

    @Column(nullable = false)
    private boolean used; // UPDATE 최종 반영 시 소모 처리(중복 사용 방지)

    @Column(length = 64)
    private String requesterId; // 요청자(회원ID/이메일). UPDATE 용도(본인 요청 확인)
    
    // 생성시 UUID 부여
    @PrePersist
    public void onCreate() {
        if (this.id == null) this.id = UUID.randomUUID().toString();
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }
}