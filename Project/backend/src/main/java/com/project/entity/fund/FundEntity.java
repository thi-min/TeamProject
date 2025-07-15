package com.project.entity.fund;

import java.sql.Date;

import jakarta.persistence.Column;
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
	@Column(name = "member_num", length = 1, nullable=false)
	private Integer memberNum; // 회원번호
	
	@Column(name = "fund_money", length = 1, nullable=false)
	private String fundMoney;	// 후원 금액임
	
	@Column(name = "fund_time", length = 1, nullable=false)
	private Date fundTime;	// 후원 일시
	
	@Column(name = "sum_money", length = 1, nullable=false)
	private String sumMoney; //후원 총 금액
}

//	CREATE TABLE chat (
//	    member_num INT AUTO_INCREMENT PRIMARY KEY,
//		fund_money VARCHAR(255),
//		fund_time DATETIME,
//		sum_money VARCHAR(255)
//	);