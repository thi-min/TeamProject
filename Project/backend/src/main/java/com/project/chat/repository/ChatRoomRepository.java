package com.project.chat.repository;

import com.project.chat.entity.ChatRoomEntity;
import com.project.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    // 1. 특정 MemberEntity에 해당하는 ChatRoomEntity를 조회합니다.
    // 회원이 채팅을 시작할 때, 기존 채팅방이 있는지 확인하는 용도로 사용됩니다.
    Optional<ChatRoomEntity> findByMember(MemberEntity member);

    // 2. 모든 ChatRoomEntity 목록을 조회합니다.
    // 관리자 페이지에서 모든 채팅방 목록을 볼 때 사용됩니다.
    List<ChatRoomEntity> findAll();

    // 3. 마지막 메시지 시간으로 정렬된 모든 채팅방 목록을 조회합니다.
    // 관리자 페이지에서 최근 대화가 있는 채팅방을 상단에 표시할 때 유용합니다.
    List<ChatRoomEntity> findAllByOrderByLastMessageTimeDesc();
    
}