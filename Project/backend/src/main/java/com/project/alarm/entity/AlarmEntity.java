package com.project.alarm.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

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