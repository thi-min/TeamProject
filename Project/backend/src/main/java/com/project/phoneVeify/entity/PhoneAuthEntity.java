package com.project.phoneVeify.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "PhoneAuth")

public class PhoneAuthEntity {
	@Id
	@Column(name = "phone_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long phoneId; // 고유값으로 PK 따로 둠
	
	@Column(name = "phone_num", nullable = false)
	private String phoneNum;	//휴대폰 번호
	
	@Column(name = "auth_code", nullable = false, length = 10)
	private String authCode;	//인증번호
	
	//Boolean true/false
	private Boolean verified = false;	//인증여부
	
	@Column(name = "request_time", nullable = false)
	//LocalDateTime 시간
	private LocalDateTime requestTime = LocalDateTime.now();//요청시간
}
