package com.project.alarm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.alarm.entity.AlarmEntity;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {
	
    List<AlarmEntity> findByMemberMemberNumOrderByAlarmTimeDesc(Long memberNum);
    // 회원번호를 조회해 alarmtime 필드 기준을 내림차순으로 정렬 (최근 알람)
}