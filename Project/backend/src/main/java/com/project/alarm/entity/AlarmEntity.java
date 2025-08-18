package com.project.alarm.entity;

import java.time.LocalDateTime;

import com.project.chat.entity.CheckState;
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
    private Long alarmId;

    @ManyToOne
    @JoinColumn(name = "member_num", nullable = false)
    private MemberEntity member;

    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_type", length = 20)
    private AlarmType alarmType;

    @Column(name = "alarm_title")
    private String alarmTitle;

    @Column(name = "alarm_content", columnDefinition = "TEXT")
    private String alarmContent;

    @Column(name = "alarm_url")
    private String alarmUrl;

    @Column(name = "alarm_time")
    private LocalDateTime alarmTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_check", length = 1)
    private CheckState alarmCheck;
}