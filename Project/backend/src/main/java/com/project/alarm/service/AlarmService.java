package com.project.alarm.service;

import com.project.alarm.entity.AlarmEntity;
import com.project.alarm.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;

    @Transactional(readOnly = true)
    public List<AlarmEntity> listByMember(Long memberNum) {
        return alarmRepository.findByMemberMemberNumOrderByAlarmTimeDesc(memberNum);
    }

    @Transactional
    public AlarmEntity create(AlarmEntity e) {
        return alarmRepository.save(e);
    }

    @Transactional
    public AlarmEntity update(AlarmEntity e) {
        return alarmRepository.save(e);
    }
    @Transactional(readOnly = true)
    public AlarmEntity get(Long id) {
        return alarmRepository.findById(id).orElse(null);
    }
}