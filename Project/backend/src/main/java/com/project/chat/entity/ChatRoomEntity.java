package com.project.chat.entity;

import java.time.LocalDateTime;
import java.util.Set;

import com.project.admin.entity.AdminEntity;
import com.project.member.entity.MemberEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_room")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long chatRoomId;//채팅방 번호

    @ManyToOne
    @JoinColumn(name = "member_num")
    private MemberEntity member;//회원 번호

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private AdminEntity admin; //관리자 id

    @Column(name = "create_at")
    private LocalDateTime createAt; //채팅방 생성 시간

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChatMessageEntity> messages; //채팅messageentity 연결
}