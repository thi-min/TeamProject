package com.project.alarm.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//AlarmResponseDto
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmResponseDto {
 private String message;            // 알림 메시지
 private LocalDateTime lastUpdateTime; // 상태 변경 시간
}
