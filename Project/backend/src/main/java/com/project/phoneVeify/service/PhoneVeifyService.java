package com.project.phoneVeify.service;

import com.project.phoneVeify.dto.PhoneAuthRequestDto;
import com.project.phoneVeify.dto.PhoneAuthVerifyDto;

//휴대폰 인증
public interface PhoneVeifyService {
    
    //인증번호 전송
    void certificationNumber(PhoneAuthRequestDto dto);
    
    //인증번호 검증
    boolean verifyCode(PhoneAuthVerifyDto dto);
    
}
