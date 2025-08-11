package com.project.adopt.dto;

import com.project.common.enums.CheckState;
import com.project.common.enums.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmDto {
    private Long alarmId;
    private Long memberNum;
    private AlarmType alarmType;
    private String alarmTitle;
    private String alarmContent;
    private String alarmUrl;
    private LocalDateTime alarmTime;
    private CheckState alarmCheck;
}