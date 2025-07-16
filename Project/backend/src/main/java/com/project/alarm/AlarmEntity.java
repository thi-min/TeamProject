package com.project.alarm;

import java.lang.reflect.Member;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alarm")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//알림창
public class AlarmEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long alarmId; //알림 번호
	
	// 회원 테이블과 연관관계 (FK)
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private Member memberNum; //회원 번호
	
	@Column(name = "alarm_center", length = 255)
    private String alarmContent; //알림 내용
	
	@Column(name = "alarm_url", length = 255)
    private String alarmURL; //알림 내용 눌렀을때 링크
	
	//기본값이 읽지 않음이라 flase 설정
	@Column(name = "is_read")
    private Boolean isRead = false; //읽음 안읽음
	
	@Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
	
	//처음 저장될때 자동으로 현재시간을 넣기위함
	@PrePersist //insert 이전에 자동으로 호출되는 메서드
	protected void onCreate() {
		//LocalDateTime.now(); 현재 시스템 시간
	    this.createdAt = LocalDateTime.now();
	}
}
