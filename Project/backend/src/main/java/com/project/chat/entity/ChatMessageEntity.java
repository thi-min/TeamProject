package com.project.chat.entity;

import com.project.admin.entity.AdminEntity;
import com.project.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_num")
    private Long messageNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_num", nullable = false)
    private ChatRoomEntity chatRoom;

    // 발신자를 명확히 분리하여, 데이터 무결성을 높입니다.
    // 둘 중 하나만 존재해야 합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num") // 회원 발신자
    private MemberEntity memberSender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_num") // 관리자 발신자
    private AdminEntity adminSender;

    @Column(name = "message_content", columnDefinition = "TEXT", nullable = false)
    private String messageContent;

    @Column(name = "sent_date", nullable = false)
    private LocalDateTime sentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_state", nullable = false)
    private CheckState checkState;

    // 편의 메서드: 발신자 ID를 반환
    public Long getSenderId() {
        if (memberSender != null) {
            return memberSender.getMemberNum();
        }
        if (adminSender != null) {
            return adminSender.getAdminNum();
        }
        return null; // 발신자가 없는 경우
    }
}