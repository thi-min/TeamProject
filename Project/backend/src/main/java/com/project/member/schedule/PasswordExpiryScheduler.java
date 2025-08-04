package com.project.member.schedule;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordExpiryScheduler {
    private final MemberRepository memberRepository;

    // 매일 새벽 00시에 실행 (cron: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 0 * * *")
    public void checkPasswordExpiry() {
        LocalDateTime expiryThreshold = LocalDateTime.now().minusDays(30);

        // 30일 이상 비밀번호 변경하지 않은 회원 목록 조회
        List<MemberEntity> expiredMembers = memberRepository.findByPwUpdatedBefore(expiryThreshold);

        for (MemberEntity member : expiredMembers) {
            // ✅ 여기에 알림 로직 삽입 (DB 저장 or 푸시 or 이메일)
            System.out.println("🔔 비밀번호 만료 대상: " + member.getMemberId());

            // 예: 알림 테이블에 저장 (추후 조회 가능하게)
            // notificationRepository.save(...);
        }
    }
}
