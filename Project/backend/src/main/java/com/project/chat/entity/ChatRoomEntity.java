package com.project.chat.entity;

import com.project.admin.entity.AdminEntity;
import com.project.member.entity.MemberEntity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_num")
    private Long roomNum; // 채팅방 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num", nullable = false)
    private MemberEntity member; // 채팅방에 참여한 회원 (N:1 관계)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_num", nullable = false)
    private AdminEntity admin; // 채팅방에 참여한 관리자 (N:1 관계)

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate; // 채팅방 생성 시간
}