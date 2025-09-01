package com.project.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.project.chat.entity.ChatEntity;
import com.project.chat.entity.ChatRoomEntity;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    // 1. 특정 ChatRoomEntity에 속한 모든 ChatEntity를 조회합니다.
    // 채팅방 상세 페이지에서 모든 대화 기록을 불러올 때 사용됩니다.
    List<ChatEntity> findByChatRoom(ChatRoomEntity chatRoom);

    // 2. 특정 ChatRoomEntity에 속한 모든 ChatEntity를 시간 순으로 정렬하여 조회합니다.
    List<ChatEntity> findByChatRoomOrderByTimestampAsc(ChatRoomEntity chatRoom);
    
    // 3. 삭제
    @Modifying
    void deleteAllByChatRoom(ChatRoomEntity chatRoom);
    
}