package com.project.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsAgreeUpdateRequestDto {
    private boolean smsAgree;
}