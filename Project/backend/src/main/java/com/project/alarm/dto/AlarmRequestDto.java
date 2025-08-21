package com.project.alarm.dto;

import java.time.LocalDateTime;

import com.project.alarm.entity.AlarmType;
import com.project.chat.entity.CheckState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmRequestDto {
    private Long alarmId; //알림 번호
    private Long memberNum; // 회원 번호
    private AlarmType alarmType; //알림 유형
    private String alarmTitle; // 알림 제목
    private String alarmContent; // 알림 내용
    private String alarmUrl; // 알림 링크
    private LocalDateTime alarmTime; // 알림 시간
    private CheckState alarmCheck; // 알림 확인
}