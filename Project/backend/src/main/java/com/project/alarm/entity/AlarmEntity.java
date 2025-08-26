package com.project.alarm.entity;

import java.time.LocalDateTime;

import com.project.member.entity.MemberEntity;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alarm")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long alarmId; //알림 번호

    @ManyToOne
    @JoinColumn(name = "member_num", nullable = false)
    private MemberEntity member;//회원 번호

    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_type", length = 20)
    private AlarmType alarmType;//알림 유형

    @Column(name = "alarm_title")
    private String alarmTitle;// 알림 제목

    @Column(name = "alarm_content", columnDefinition = "TEXT")
    private String alarmContent;// 알림 내용

    @Column(name = "alarm_url")
    private String alarmUrl; // 알림 링크

    @Column(name = "alarm_time")
    private LocalDateTime alarmTime; // 알림 시간

    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_check", length = 1)
    private AlarmCheck alarmCheck; //알림 확인 
}