package com.project.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelfPasswordUpdateRequestDto {
    private String currentPassword;
    private String newPassword;
    private String newPasswordCheck;
}