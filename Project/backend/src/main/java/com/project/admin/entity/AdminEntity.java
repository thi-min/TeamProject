package com.project.admin.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.member.entity.MemberEntity;
import com.project.member.entity.MemberSex;
import com.project.member.entity.MemberState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
	@Column(name = "admin_id",nullable = false)
	private Long adminId; //관리자 아이디
	
	//왜래키
	@OneToMany(fetch = FetchType.LAZY) // 다대일 관계
	//참조할 키 member_num
	@JoinColumn(name = "member_num", nullable = false)
	//Member 상대 테이블 
	//추후 작업된 MemberEntity 보고 작성
	private MemberEntity member; //회원번호
	
	@Column(name = "admin_email",nullable = false)
	private String adminEmail; //이메일
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

}