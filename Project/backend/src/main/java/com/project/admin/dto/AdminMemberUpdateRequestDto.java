package com.project.admin.dto;

import com.project.admin.dto.AdminLoginResponseDto.AdminLoginResponseDtoBuilder;
import com.project.member.entity.MemberState;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class AdminMemberUpdateRequestDto {
    private boolean memberLock; // 잠금 여부
    private MemberState memberState; // 상태 (enum: ACTIVE, REST, OUT)
    private String message; //상태 + 잠금 메시지
}
