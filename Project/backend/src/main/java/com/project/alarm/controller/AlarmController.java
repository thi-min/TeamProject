package com.project.alarm.controller;

import com.project.alarm.entity.AlarmEntity;
import com.project.alarm.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;

    // 회원별 알림 목록
    @GetMapping("/member/{memberNum}")
    public ResponseEntity<List<AlarmEntity>> listByMember(@PathVariable Long memberNum) {
        return ResponseEntity.ok(alarmService.listByMember(memberNum));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlarmEntity> get(@PathVariable Long id) {
        AlarmEntity e = alarmService.get(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(e);
    }

    @PostMapping
    public ResponseEntity<AlarmEntity> create(@RequestBody AlarmEntity alarm) {
        return ResponseEntity.ok(alarmService.create(alarm));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlarmEntity> update(@PathVariable Long id, @RequestBody AlarmEntity alarm) {
        if (alarm.getAlarmId() == null) alarm.setAlarmId(id);
        AlarmEntity updated = alarmService.update(alarm);
        return ResponseEntity.ok(updated);
    }
}