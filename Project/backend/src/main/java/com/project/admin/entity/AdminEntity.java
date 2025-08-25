package com.project.admin.entity;

import java.time.LocalDateTime;

import com.project.member.entity.MemberEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Admin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminEntity {
	
	//기본
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "admin_num",nullable = false)
	private Long adminNum; //관리자 번호
	
	@Column(nullable = false, unique = true, length = 100)
	private String adminId; //관리자 아이디
	
	@ManyToOne
	@JoinColumn(name = "member_num")
	private MemberEntity member; // ✅ 필드명도 가능하면 의미 있는 이름으로
	
//	@Column(name = "admin_email",nullable = false)
//	private String adminEmail; //이메일
	@Column(name = "admin_pw",nullable = false)
	private String adminPw; //비밀번호
	@Column(name = "admin_name",nullable = false)
	private String adminName; //이름
	
	@Column(name = "admin_phone",nullable = false)
	private String adminPhone; //휴대폰번호
	
//	@Column(name = "authority")
//	@Enumerated(EnumType.STRING)  
//	private AdminAuthority Authority; //관리자 권한
//	
//	@Column(name = "admin_state")
//	@Enumerated(EnumType.STRING)  
//	private AdminState AdminState; //관리자 상태
	
	@Column(name = "regist_date", nullable = false)
	private LocalDateTime registDate; //등록일시
	@Column(name = "connect_data", nullable = false)
	private LocalDateTime connectData; //접속일시
	
    private String accessToken;
    private String refreshToken;
}