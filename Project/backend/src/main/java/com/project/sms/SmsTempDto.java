package com.project.sms;

import lombok.Data;

@Data
public class SmsTempDto {
	
	private Long templateId;	//템플릿 번호
	private String content;	//문자 내용
    
}
