package com.project.alarm.dto;

import java.time.LocalDateTime;

import com.project.alarm.entity.AlarmType;
import com.project.alarm.entity.CheckState;

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
public class AlarmResponseDto {
    private Long alarmId;
    private Long memberNum;
    private AlarmType alarmType;
    private String alarmTitle;
    private String alarmContent;
    private String alarmUrl;
    private LocalDateTime alarmTime;
    private CheckState alarmCheck;
}