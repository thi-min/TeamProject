package com.project.phoneVeify.dto;

import jakarta.validation.constraints.Pattern;

public class PhoneAuthRequestDto {
	
    @Pattern(regexp = "^010\\d{8}$", message = "올바른 휴대폰 번호 형식이어야 합니다.")
    private String phoneNum;
     
}
