package com.project.chat.entity;

import java.time.LocalDateTime;

import com.project.admin.entity.AdminEntity;
import com.project.alarm.entity.CheckState;
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
@Table(name = "chat_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long chatMessageId;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoomEntity chatRoom;

    @ManyToOne
    @JoinColumn(name = "member_num")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private AdminEntity admin;

    @Column(name = "send_time")
    private LocalDateTime sendTime;

    @Column(name = "chat_cont", columnDefinition = "TEXT")
    private String chatCont;

    @Enumerated(EnumType.STRING)
    @Column(name = "chat_check", length = 1)
    private CheckState chatCheck;
}