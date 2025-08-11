package com.project.alarm.dto;

import com.project.common.enums.AlarmType;
import com.project.common.enums.CheckState;
import lombok.*;
import java.time.LocalDateTime;

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