package com.project.alarm.repository;

import com.example.adopt.domain.Alarm;
import com.example.adopt.domain.CheckStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    // 회원별 알림 조회 (최신순)
    List<Alarm> findByMember_MemberNumOrderByAlarmTimeDesc(Long memberNum);

    // 확인여부 별 조회
    List<Alarm> findByAlarmCheck(CheckStatus check);
}