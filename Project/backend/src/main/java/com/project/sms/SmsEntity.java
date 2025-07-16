package com.project.sms;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@AllArgsConstructor //클래스에 있는 모든 필드를 인자로 받는 생성자를 자동생성
@NoArgsConstructor //매개변수가 없는 기본 생성자를 자동생성
@Getter
@Setter

public class SmsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long templateId;	//템플릿 번호
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;	//문자 내용
}
