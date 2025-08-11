package com.project.chat.repository;

import com.project.chat.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    Optional<ChatRoomEntity> findByChatRoomId(Long chatRoomId);
    Optional<ChatRoomEntity> findByMemberMemberNum(Long memberNum);
}