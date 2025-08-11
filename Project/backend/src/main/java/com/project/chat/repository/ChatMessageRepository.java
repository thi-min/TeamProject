package com.project.chat.repository;

import com.example.adopt.domain.ChatMessage;
import com.example.adopt.dto.ChatMessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 채팅방의 메시지 목록(오름차순)
    List<ChatMessage> findByChatRoom_ChatRoomIdOrderBySendTimeAsc(Long chatRoomId);

    // 채팅방의 메시지 목록을 DTO로 (member 이름 포함)
    @Query("select new com.example.adopt.dto.ChatMessageDto(cm.chatMessageId, cr.chatRoomId, m.memberNum, m.name, cm.adminId, cm.sendTime, cm.chatCont) " +
           "from ChatMessage cm join cm.chatRoom cr left join cm.member m " +
           "where cr.chatRoomId = :chatRoomId order by cm.sendTime asc")
    List<ChatMessageDto> findMessagesDtoByChatRoomId(Long chatRoomId);

    // 페이징된 메시지 (예: 최신 n개)
    Page<ChatMessage> findByChatRoom_ChatRoomId(Long chatRoomId, Pageable pageable);
}