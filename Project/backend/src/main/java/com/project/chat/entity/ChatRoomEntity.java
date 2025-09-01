package com.project.chat.entity;

import java.time.LocalDateTime;

import com.project.member.entity.MemberEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "chat_rooms")
public class ChatRoomEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long chatRoomNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num", unique = true, nullable = false)
    private MemberEntity member;

    @Column(nullable = false)
    private String lastMessage;

    @Column(nullable = false)
    private LocalDateTime lastMessageTime;

    // 생성자 및 편의 메서드 추가 가능
}