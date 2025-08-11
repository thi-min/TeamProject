package com.project.alarm.repository;

import com.project.alarm.entity.AlarmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {
    List<AlarmEntity> findByMemberMemberNumOrderByAlarmTimeDesc(Long memberNum);
}