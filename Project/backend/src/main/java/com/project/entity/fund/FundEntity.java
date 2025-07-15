package com.project.entity.fund;

import java.sql.Date;

import jakarta.persistence.Entity;
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
@Table(name = "fund")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FundEntity {
	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer member_num; // 회원번호
	
	private String fund_money;	// 후원 금액
	
	private Date fund_time;	// 후원 일시
	
	private String sum_money; //후원 총 금액
}

//	CREATE TABLE chat (
//	    member_num INT AUTO_INCREMENT PRIMARY KEY,
//		fund_money VARCHAR(255),
//		fund_time DATETIME,
//		sum_money VARCHAR(255)
//	);