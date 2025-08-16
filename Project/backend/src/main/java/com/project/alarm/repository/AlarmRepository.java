package com.project.alarm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.alarm.entity.AlarmEntity;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {
    List<AlarmEntity> findByMemberMemberNumOrderByAlarmTimeDesc(Long memberNum);
}