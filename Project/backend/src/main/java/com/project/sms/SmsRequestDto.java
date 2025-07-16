package com.project.sms;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class SmsRequestDto {
	private List<String> phoneList; //수신자 전화번호 목록
    private String message; //메시지 내용
    private String smsKey; //인증키
}
