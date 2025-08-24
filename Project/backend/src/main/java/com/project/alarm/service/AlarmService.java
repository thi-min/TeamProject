package com.project.alarm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.alarm.entity.AlarmEntity;
import com.project.alarm.repository.AlarmRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;
    
    // 특정 회원 번호의 데이터 내림차순으로 정렬 반환
    @Transactional(readOnly = true)
    public List<AlarmEntity> listByMember(Long memberNum) {
        return alarmRepository.findByMemberMemberNumOrderByAlarmTimeDesc(memberNum);
    }
    //알림 데이터 조회
    @Transactional(readOnly = true)
    public AlarmEntity get(Long id) {
        return alarmRepository.findById(id).orElse(null);
    }
    // 알림 데이터 저장
    @Transactional
    public AlarmEntity create(AlarmEntity e) {
        return alarmRepository.save(e);
    }
    // 알림 데이터 갱신
    @Transactional
    public AlarmEntity update(AlarmEntity e) {
        return alarmRepository.save(e);
    }
    // 알림 제거
    @Transactional
    public void delete(Long id) {
        alarmRepository.deleteById(id);
    }
}