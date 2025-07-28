package com.project.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class AdminMemberLockChangeDto {
	private Long memberNum;
	private String message;
}
