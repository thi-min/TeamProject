package com.project.chat.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * WebSocket 연결(세션) 관리용 엔티티.
 * - 1:1 채팅 시스템에서 특정 채팅방(chatRoom)과 WebSocket sessionId를 연계하여
 *   연결/종료/재연결을 관리할 수 있게 함.
 */
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
    @Column(name = "session_id_pk")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoomEntity chatRoom;

    @Column(name = "ws_session_id", nullable = false, unique = true)
    private String wsSessionId; // WebSocket 세션 아이디 (예: SimpMessageHeaderAccessor.getSessionId())

    @Column(name = "principal_type", length = 20)
    private String principalType; // "MEMBER" or "ADMIN" (간단하게 String 사용, 필요 시 enum으로 교체)

    @Column(name = "principal_value")
    private String principalValue; // 회원번호(memberNum) 또는 adminId 등 식별자(문자열형태)

    @Column(name = "connected_at")
    private LocalDateTime connectedAt;

    @Column(name = "last_ping")
    private LocalDateTime lastPing;

    @Column(name = "closed")
    private boolean closed;
}