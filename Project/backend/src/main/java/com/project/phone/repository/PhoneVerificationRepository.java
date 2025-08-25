package com.project.phone.repository;

import com.project.phone.entity.PhoneVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, String> {

    /**
     * 최신 요청을 찾고 싶을 때 사용(예: 재전송 간격 체크)
     */
    Optional<PhoneVerification> findFirstByPhoneNumberAndPurposeOrderByCreatedAtDesc(String phoneNumber, String purpose);

    /**
     * 검증 가능한 코드 조회
     * - 아직 만료 전
     * - verified=false
     * - used=false
     * - 코드 일치
     */
    Optional<PhoneVerification> findFirstByIdAndPhoneNumberAndPurposeAndCodeAndVerifiedFalseAndUsedFalseAndExpiresAtAfter(
            String id, String phoneNumber, String purpose, String code, LocalDateTime now
    );

    /**
     * UPDATE 최종 반영 시 사용가능한(=이미 verified=true, used=false, 만료 전) 토큰 조회
     */
    Optional<PhoneVerification> findFirstByIdAndPhoneNumberAndPurposeAndVerifiedTrueAndUsedFalseAndExpiresAtAfter(
            String id, String phoneNumber, String purpose, LocalDateTime now
    );
}