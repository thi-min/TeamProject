package com.project.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chat.entity.ChatRoomEntity;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    Optional<ChatRoomEntity> findByChatRoomId(Long chatRoomId);
    //채팅방 아이디 조회
    Optional<ChatRoomEntity> findByMemberMemberNum(Long memberNum);
    //회원번호 조회
}