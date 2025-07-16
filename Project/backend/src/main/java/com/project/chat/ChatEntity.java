package com.project.chat;

import java.sql.Date;

import com.project.admin.AdminEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "chat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer member_num; //회원번호
	
	@ManyToOne
    @JoinColumn(name = "admin_id", nullable=false) // 참조할 테이블의 PK 컬럼명
    private AdminEntity adminId;
	//관리자 아이디
	
	@Column(name = "chat_cont", nullable=false)
	private String chatCont; // 대화 내용
	
	@Column(name = "send_time",nullable=false)
	private Date sendTime; // 보낸 시간
	
	@Column(name = "take_time", nullable=false)
	private Date takeTime; // 받은 시간

	@Enumerated(EnumType.STRING)
	@Column(name = "chat_check", length = 1, nullable=false)
	private ChatCheck chatCheck;  //확인 상태
	

}
//
//CREATE TABLE admin (
//	    admin_id VARCHAR(100) PRIMARY KEY,
//	    -- 기타 관리자 정보
//	);
//
//	CREATE TABLE chat (
//	    member_num INT AUTO_INCREMENT PRIMARY KEY,
//	    manage_num INT,
//	    admin_id VARCHAR(255),
//	    chat_cont TEXT,
//	    send_time DATETIME,
//	    take_time DATETIME,
//	    chat_check ENUM('Y','N'),
//
//	);
