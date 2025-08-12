package com.project.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chat.entity.ChatMessageEntity;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByChatRoomChatRoomIdOrderBySendTimeAsc(Long chatRoomId);
    List<ChatMessageEntity> findTop1ByChatRoomChatRoomIdOrderBySendTimeDesc(Long chatRoomId);
}