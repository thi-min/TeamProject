package com.project.sms;

import java.util.Map;
import lombok.Data;

@Data
//인증상태 여부 확인
public class SmsResponseDto {
	private Map<String, String> phoneMap; //성공, 실패
}
