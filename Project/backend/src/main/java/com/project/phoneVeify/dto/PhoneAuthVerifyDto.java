package com.project.phoneVeify.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

//요청한 핸드폰번호로 인증번호 호출
public class PhoneAuthVerifyDto {
	
    private String phoneNum;

    @NotBlank(message = "인증번호는 필수입니다.")
    private String authNum;
}
