package com.project.alarm.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.alarm.dto.AlarmRequestDto;
import com.project.alarm.dto.AlarmResponseDto;
import com.project.alarm.entity.AlarmEntity;
import com.project.alarm.mapper.AlarmMapper;
import com.project.alarm.service.AlarmService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;
    private final AlarmMapper alarmMapper;

    @GetMapping("/member/{memberNum}")
    public ResponseEntity<List<AlarmResponseDto>> listByMember(@PathVariable Long memberNum) {
        List<AlarmEntity> list = alarmService.listByMember(memberNum);
        return ResponseEntity.ok(list.stream().map(alarmMapper::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlarmResponseDto> get(@PathVariable Long id) {
        AlarmEntity e = alarmService.get(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(alarmMapper.toDto(e));
    }

    @PostMapping
    public ResponseEntity<AlarmResponseDto> create(@RequestBody AlarmRequestDto req) {
        AlarmEntity entity = alarmMapper.toEntity(req);
        AlarmEntity saved = alarmService.create(entity);
        return ResponseEntity.ok(alarmMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlarmResponseDto> update(@PathVariable Long id, @RequestBody AlarmRequestDto req) {
        AlarmEntity exist = alarmService.get(id);
        if (exist == null) return ResponseEntity.notFound().build();
        AlarmEntity entity = alarmMapper.toEntity(req);
        entity.setAlarmId(id);
        return ResponseEntity.ok(alarmMapper.toDto(alarmService.update(entity)));
    }
}