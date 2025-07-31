package com.project.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class MemberIdCheckResponseDto {
    private boolean exists;   // true: 이미 존재함
    private String message;   // 사용자에게 보여줄 메시지
}
