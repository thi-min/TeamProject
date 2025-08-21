package com.project.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chat.entity.ChatMessageEntity;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByChatRoomChatRoomIdOrderBySendTimeAsc(Long chatRoomId);
    //채팅방번호를 확인해 보낸시간 내림차순으로 정렬 조회
    List<ChatMessageEntity> findTop1ByChatRoomChatRoomIdOrderBySendTimeDesc(Long chatRoomId);
    //채팅방번호를 확인해 보낸시간 내림차순으로 정렬 후 가장 최근 1가지 조회
}