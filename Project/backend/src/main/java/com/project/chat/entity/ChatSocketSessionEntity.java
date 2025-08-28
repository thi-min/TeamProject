package com.project.chat.entity;

import com.project.admin.entity.AdminEntity;
import com.project.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_socket_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSocketSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "socket_id", nullable = false, unique = true)
    private String socketId;

    // 세션은 한 명의 회원 또는 관리자와 연결됩니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num")
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_num")
    private AdminEntity admin;

    @Column(name = "connected_at", nullable = false)
    private LocalDateTime connectedAt;
}