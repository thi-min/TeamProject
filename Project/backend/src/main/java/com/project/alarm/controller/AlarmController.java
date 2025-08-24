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
import com.project.alarm.entity.AlarmType;
import com.project.alarm.service.AlarmService;
import com.project.chat.entity.CheckState;
import com.project.member.entity.MemberEntity;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;

    // DTO와 Entity를 수동으로 변환하는 메서드 추가
    private AlarmEntity toEntity(AlarmRequestDto dto) {
        if (dto == null) {
            return null;
        }

        AlarmEntity entity = new AlarmEntity();
        entity.setAlarmId(dto.getAlarmId());
        entity.setAlarmType(dto.getAlarmType());
        entity.setAlarmTitle(dto.getAlarmTitle());
        entity.setAlarmContent(dto.getAlarmContent());
        entity.setAlarmUrl(dto.getAlarmUrl());
        entity.setAlarmTime(dto.getAlarmTime());
        entity.setAlarmCheck(dto.getAlarmCheck());

        if (dto.getMemberNum() != null) {
            MemberEntity member = new MemberEntity();
            member.setMemberNum(dto.getMemberNum());
            entity.setMember(member);
        }

        return entity;
    }

    private AlarmResponseDto toDto(AlarmEntity entity) {
        if (entity == null) {
            return null;
        }

        AlarmResponseDto dto = new AlarmResponseDto();
        dto.setAlarmId(entity.getAlarmId());
        dto.setAlarmType(entity.getAlarmType());
        dto.setAlarmTitle(entity.getAlarmTitle());
        dto.setAlarmContent(entity.getAlarmContent());
        dto.setAlarmUrl(entity.getAlarmUrl());
        dto.setAlarmTime(entity.getAlarmTime());
        dto.setAlarmCheck(entity.getAlarmCheck());
        
        if (entity.getMember() != null) {
            dto.setMemberNum(entity.getMember().getMemberNum());
        }

        return dto;
    }
    //회원별 알림 조회
    @GetMapping("/member/{memberNum}")
    public ResponseEntity<List<AlarmResponseDto>> listByMember(@PathVariable Long memberNum) {
        List<AlarmEntity> list = alarmService.listByMember(memberNum);
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }
    //단일 알림 조회
    @GetMapping("/{id}")
    public ResponseEntity<AlarmResponseDto> get(@PathVariable Long id) {
        AlarmEntity e = alarmService.get(id);
        if (e == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDto(e));
    }
    //알림 생성
    @PostMapping
    public ResponseEntity<AlarmResponseDto> create(@RequestBody AlarmRequestDto req) {
        AlarmEntity entity = toEntity(req);
        AlarmEntity saved = alarmService.create(entity);
        return ResponseEntity.ok(toDto(saved));
    }
    //알림 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alarmService.delete(id);
        return ResponseEntity.noContent().build();
    }
}