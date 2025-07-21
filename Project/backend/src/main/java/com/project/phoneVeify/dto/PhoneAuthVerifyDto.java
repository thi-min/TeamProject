package com.project.phoneVeify.dto;

import jakarta.validation.constraints.NotBlank;

public class PhoneAuthVerifyDto {
	
    private String phoneNum;

    @NotBlank(message = "인증번호는 필수입니다.")
    private String authNum;
}
