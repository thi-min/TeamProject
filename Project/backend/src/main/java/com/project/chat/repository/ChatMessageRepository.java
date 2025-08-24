package com.project.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chat.entity.ChatMessageEntity;
import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.entity.CheckState;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    
    // 채팅방번호를 확인해 보낸시간 오름차순으로 정렬 조회 (기존)
    List<ChatMessageEntity> findByChatRoomChatRoomIdOrderBySendTimeAsc(Long chatRoomId);
    
    // 채팅방번호를 확인해 보낸시간 내림차순으로 정렬 후 가장 최근 1가지 조회
    // 반환 타입을 Optional<ChatMessageEntity>로 변경하여 null 처리 용이하게 함
    Optional<ChatMessageEntity> findTop1ByChatRoomChatRoomIdOrderBySendTimeDesc(Long chatRoomId);
    
    // 특정 채팅방에 미확인(N) 상태의 메시지가 존재하는지 확인하는 메서드 추가
    boolean existsByChatRoomAndChatCheck(ChatRoomEntity chatRoom, CheckState chatCheck);
    
    // 채팅방 제거 (기존)
    void deleteByChatRoom(ChatRoomEntity chatRoom);
}