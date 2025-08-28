package com.project.alarm.dto;

import java.time.LocalDateTime;

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
	private Long memberNum;       // 알람 대상 회원 번호
	private String message;       // 알람 내용
	private String url;           // 클릭 시 이동 링크
	private LocalDateTime alarmTime; // 알람 생성 시각
}