package com.project.chat.repository;

import com.project.chat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByChatRoomChatRoomIdOrderBySendTimeAsc(Long chatRoomId);
    List<ChatMessageEntity> findTop1ByChatRoomChatRoomIdOrderBySendTimeDesc(Long chatRoomId);
}