package com.project.chat.repository;

import com.example.adopt.domain.ChatRoom;
import com.example.adopt.dto.ChatRoomListDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 각 채팅방에 대해 마지막 메시지 내용을 포함한 요약 목록 반환 (관리자 목록용)
    @Query("select new com.example.adopt.dto.ChatRoomListDto(cr.chatRoomId, m.memberNum, m.name, cm.chatCont, cm.sendTime) " +
           "from ChatRoom cr join cr.member m, ChatMessage cm " +
           "where cm.chatRoom = cr and cm.sendTime = (select max(cm2.sendTime) from ChatMessage cm2 where cm2.chatRoom = cr) " +
           "order by cm.sendTime desc")
    List<ChatRoomListDto> findChatRoomSummaries(Pageable pageable);
}