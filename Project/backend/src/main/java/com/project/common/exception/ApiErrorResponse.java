package com.project.common.exception;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiErrorResponse {
	private int status;
    private String error;
    private String message; // ← 여기에 각 상황별 메시지를 "넣어주는" 것
    private LocalDateTime timestamp;
}
