package com.project.entity.chat;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	private Integer member_num;
	
	private Integer manage_num;
	
//	@ManyToOne
//    @JoinColumn(name = " ???? ") // 참조할 테이블의 PK 컬럼명
//    private AdminEntity admin;
	private String admin_id;
	
	private String chat_cont;
	
	private Date send_time;
	
	private Date take_time;

	@Enumerated(EnumType.STRING)
	@Column(name = "chat_check", length = 1)
	private ChatCheck chatCheck;
	

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
